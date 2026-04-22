(() => {
    'use strict'
	const siteUrl = document.querySelector('html').dataset.siteUrl;
	
	/**
	 * Xử lý click button delete 1 object
	 */
	function processDeleteModal(modal, event) {
		if (modal.id !== 'deleteModal') return;
		
        // Thẻ <a> kích hoạt modal
        let anchor = event.relatedTarget;

        // Trích xuất thông tin
        let deleteUrl = anchor.getAttribute('href');
        let message = anchor.getAttribute('data-message');
		let preMessage = anchor.getAttribute('data-pre-message');

        // Cập nhật nội dung Modal
        let modalMessage = modal.querySelector('#delete-message');
		let modalPreMessage = modal.querySelector('#delete-pre-message');
        let modalBtnDelete = modal.querySelector('#confirm-delete-link');

		modalPreMessage.innerHTML = preMessage;
        modalMessage.innerHTML = message;
        modalBtnDelete.setAttribute('href', deleteUrl);
	}
	
	/**
	 * Xử lý nghiệp vụ show modal với ajax url
	 */
	function processAjaxModal(modal, event) {
		let button = event.relatedTarget;
        let url = button.dataset.modalUrl;
		
		let modalDialog = modal.querySelector('.modal-dialog');
		if (modalDialog) {
			modalDialog.classList.remove("modal-sm", "modal-lg", "modal-xl");
				
			if (button.dataset.modalSize) {
				modalDialog.classList.add(`modal-${button.dataset.modalSize}`);
				
			}
		}
		
		
        let modalContent = modal.querySelector('.modal-content');
		
    	modalContent.innerHTML = `<div class="text-center p-5">
					            <div class="spinner-border text-primary display-1"></div>
					            <p class="pt-2">Đang tải dữ liệu...</p>
					         </div>`;

        fetch(url).then(async (resp) => {
			const errorPages = [403, 404, 500];
			
		    if (errorPages.includes(resp.status)) {
				const errorPage = resp.status == 404 ? '404' : 'error';
		        window.location.href = `${siteUrl}${errorPage}`; // Chuyển hướng đến trang error
		        return;
		    }
			
            if (!resp.ok) throw new Error('Không thể kết nối đến máy chủ');
			if (resp.redirected) {
				const html = await resp.text();
                const parser = new DOMParser();
                const doc = parser.parseFromString(html, 'text/html');
				const flatMessage = doc.querySelector(".flash-message");
                if (flatMessage) {
					sessionStorage.setItem('flash_message', flatMessage.innerHTML);	
				}
                
                window.location.href = resp.url;
				return;
			}
            return resp.text();
        }). then((html) => {
            modalContent.innerHTML = html;
        });
	}
	
	/**
	 * Xử lý nghiệp vụ ajax form
	 */
	function processAjaxForm(form, event) {
		event.preventDefault();
		event.stopPropagation();

	    const formData = new FormData(form);
	    const url = form.getAttribute('action');
	    const method = form.getAttribute('method') || 'POST';
		event.submitter.insertAdjacentHTML('afterbegin', `<i class="fa fa-spin fa-spinner me-2"></i>`);
		
		if (form.closest('.ajax-form-container')) {
			form.closest('.ajax-form-container').classList.add('is-loading');
		}
		else {
			form.classList.add('is-loading')	
		}

	    fetch(url, {
	        method: method,
	        body: formData,
	        headers: {
	            // Thêm header này để Spring Security không chặn nếu có dùng CSRF
	            'X-Requested-With': 'XMLHttpRequest'
	        }
	    })
	    .then(async resp => {
			const errorPages = [403, 404, 500];						
		    if (errorPages.includes(resp.status)) {
				const errorPage = resp.status == 404 ? '404' : 'error';
		        window.location.href = `${siteUrl}${errorPage}`; // Chuyển hướng đến trang error
				
		        return;
		    }
			
			// 2. Kiểm tra nếu có Redirect (Phản hồi từ server có redirected = true)
		    if (resp.redirected) {
		        const html = await resp.text();
		        const parser = new DOMParser();
		        const doc = parser.parseFromString(html, 'text/html');
		        
		        const flashMsg = doc.querySelector(".flash-message");
		        if (flashMsg) {
		            sessionStorage.setItem('flash_message', flashMsg.innerHTML);
		        }
		        
		        window.location.href = resp.url;
		        return;
		    }
				
	        if (resp.ok) {
				const text = await resp.text();
		        const parser = new DOMParser();
		        const doc = parser.parseFromString(text, 'text/html');
		        const flashMsg = doc.querySelector(".flash-message");
				const modalContent = form.closest('.modal-content');
				
				if (flashMsg && modalContent) {
		            
					form.classList.remove('is-loading');
					if (form.closest('.ajax-form-container')) {
						form.closest('.ajax-form-container').classList.remove('is-loading');
					}
					
					modalContent.innerHTML = text;
					
		        } else {
		            window.location.reload();
		        }
				
		        return;
	        }
	    })
	    .catch(error => console.error('Error:', error));
	}
	
	/**
	 * Xử lý nghiệp vụ submit form
	 */
	document.addEventListener('submit', function (event) {
	    const form = event.target.closest('form');
		
		/**
		 * Validate cho form
		 */
		if (form.classList.contains('needs-validation') && !form.checkValidity()) {
            event.preventDefault()
            event.stopPropagation();
			form.classList.add('was-validated'); 
			return;
        }
		
		/**
		 * Remove tham số empty cho filter form
		 */
		if (form.method.toLowerCase() === 'get' && form.classList.contains('filter-form')) {
			const inputs = form.querySelectorAll('input, select, textarea');
			console.log(inputs);
            inputs.forEach(input => {
                if (!input.value || input.value.trim() === "") {
                    input.disabled = true;
                }
            });
			
		}

		/**
		 * Xử lý với ajaxForm
		 */
	    if (form.classList.contains('ajax-form')) {
			processAjaxForm(form, event);	
		}	    
	});
	
	document.addEventListener('show.bs.modal', function (event) {
	    const modal = event.target.closest('.modal');
	    
	    // Nếu không phải modal thì bỏ qua
	    if (!modal) return;
		
		if (modal.id == 'deleteModal') {
			processDeleteModal(modal, event);	
		}

        // Button modal phải có data-ajax-url
        if (event.relatedTarget.dataset.modalUrl) {
			processAjaxModal(modal, event);	
		}        
	});
	
		

	// Xử lý flashMessage lúc redirect
	document.addEventListener('DOMContentLoaded', () => {
	    const msg = sessionStorage.getItem('flash_message');
	    if (msg) {
	        document.querySelector('.flash-message').innerHTML = msg;
	        sessionStorage.removeItem('flash_message'); // Xóa ngay sau khi dùng
	    }
	});


    // Init chartjs
    const PROFESSIONAL_PALETTE = [
        { border: '#4F46E5', bg: 'rgba(79, 70, 229, 0.1)' },   // Indigo
        { border: '#10B981', bg: 'rgba(16, 185, 129, 0.1)' },   // Emerald
        { border: '#F59E0B', bg: 'rgba(245, 158, 11, 0.1)' },   // Amber
        { border: '#EF4444', bg: 'rgba(239, 68, 68, 0.1)' },    // Rose
        { border: '#06B6D4', bg: 'rgba(6, 182, 212, 0.1)' },    // Cyan
        { border: '#8B5CF6', bg: 'rgba(139, 92, 246, 0.1)' },   // Violet
        { border: '#F43F5E', bg: 'rgba(244, 63, 94, 0.1)' },    // Pink
        { border: '#3B82F6', bg: 'rgba(59, 130, 246, 0.1)' },   // Blue
        { border: '#14B8A6', bg: 'rgba(20, 184, 166, 0.1)' },   // Teal
        { border: '#F97316', bg: 'rgba(249, 115, 22, 0.1)' },   // Orange
        { border: '#84CC16', bg: 'rgba(132, 204, 22, 0.1)' },   // Lime
        { border: '#0EA5E9', bg: 'rgba(14, 165, 233, 0.1)' },   // Sky
        { border: '#A855F7', bg: 'rgba(168, 85, 247, 0.1)' },   // Purple
        { border: '#EC4899', bg: 'rgba(236, 72, 153, 0.1)' },   // Fuchsia
        { border: '#6366F1', bg: 'rgba(99, 102, 241, 0.1)' },   // Violet-Indigo
        { border: '#22C55E', bg: 'rgba(34, 197, 94, 0.1)' },    // Green
        { border: '#EAB308', bg: 'rgba(234, 179, 8, 0.1)' },    // Yellow
        { border: '#D946EF', bg: 'rgba(217, 70, 239, 0.1)' },   // Light Purple
        { border: '#065F46', bg: 'rgba(6, 95, 70, 0.1)' },     // Dark Green
        { border: '#1E293B', bg: 'rgba(30, 41, 59, 0.1)' }      // Slate
    ];

    const DEFAULT_CHART_OPTIONS = {
        responsive: true,
        maintainAspectRatio: true,
        plugins: {
            legend: {
                display: true,
                position: 'bottom',
                labels: { usePointStyle: true, padding: 20, font: { size: 12, family: "'Inter', sans-serif" } }
            },
            tooltip: {
                backgroundColor: '#1e293b',
                padding: 12,
                boxPadding: 8,
                usePointStyle: true
            }
        }
    };

    document.querySelectorAll('.chart').forEach(chart => {
        const configStr = chart.dataset.options;
        if (!configStr || typeof Chart === 'undefined') return;

        try {
            const rawConfig = JSON.parse(configStr);
            const type = rawConfig.type;

            // 1. Cấu hình Options tùy biến theo loại Chart
            let dynamicOptions = { ...DEFAULT_CHART_OPTIONS };

            if (['bar', 'line'].includes(type)) {
                dynamicOptions.scales = {
                    y: { beginAtZero: true, grid: { drawBorder: false, color: '#f1f5f9' } },
                    x: { grid: { display: false } }
                };
            }

            // 2. Xử lý Stylist cho từng Dataset
            rawConfig.data.datasets.forEach((dataset, i) => {
                const theme = PROFESSIONAL_PALETTE[i % PROFESSIONAL_PALETTE.length];

                if (type === 'line') {
                    dataset.borderColor = theme.border;
                    dataset.backgroundColor = theme.bg;
                    dataset.fill = true;
                    dataset.tension = 0.4;
                    dataset.pointRadius = 4;
                    dataset.pointHoverRadius = 6;
                    dataset.borderWidth = 3;
                }
                else if (type === 'bar') {
                    dataset.backgroundColor = theme.border;
                    dataset.hoverBackgroundColor = theme.border.replace('1)', '0.8)');
                    dataset.borderRadius = 6;
                    
                }
                else if (['pie', 'doughnut'].includes(type)) {
                    dataset.backgroundColor = PROFESSIONAL_PALETTE.map(p => p.border);
                    dataset.hoverOffset = 15;
                    dataset.borderWidth = 2;
                    dataset.borderColor = '#ffffff';
                    if (type === 'doughnut') {
                        dynamicOptions.cutout = '75%';
                    }
                }
            });

            // 3. Khởi tạo
            new Chart(chart.getContext('2d'), {
                type: type,
                data: rawConfig.data,
                options: dynamicOptions
            });

        } catch (e) {
            console.error("Chart.js Error:", e);
        }
    });
	
	document.addEventListener('input', function(event) {
		if (!event.target.closest('[data-match-field]')) {
			return;
		}
		const currentField = event.target;
		const matchField = document.getElementById(currentField.dataset.matchField);
		if (!matchField) {
			return;							
		}
			
		if (currentField.value !== matchField.value) {
			currentField.setCustomValidity("Không trùng khớp");
		}
		else {
			currentField.setCustomValidity("");
		}
	});
	
	document.addEventListener('input', function (event) {
	    const changedFieldId = event.target.id;
	    // Tìm xem có ô nào đang "đợi" ô vừa sửa này không
	    const dependentField = document.querySelector(`[data-match-field="${changedFieldId}"]`);
	    
	    if (dependentField) {
	        if (dependentField.value !== event.target.value) {
	            dependentField.setCustomValidity("Không trùng khớp");
	        } else {
	            dependentField.setCustomValidity("");
	        }
	    }
	});
	
	
	document.addEventListener("DOMContentLoaded", function () {
	    // 1. Lấy tất cả các tham số hiện tại trên URL (keyword, category, filter...)
	    const currentParams = new URLSearchParams(window.location.search);
	    
	    // 2. Tìm tất cả các link trong pagination fragment
	    const pageLinks = document.querySelectorAll('.pagination .page-link');

	    pageLinks.forEach(link => {
	        // Bỏ qua các link không có href (như dấu ... hoặc nút bị disabled)
	        if (!link.getAttribute('href') || link.getAttribute('href') === '#') return;

	        try {
	            // Tạo một đối tượng URL từ href của thẻ <a>
	            const url = new URL(link.href, window.location.origin);
	            
	            // 3. Duyệt qua các params hiện tại của trình duyệt
	            currentParams.forEach((value, key) => {
	                // Chỉ thêm vào nếu link phân trang chưa có tham số này 
	                // (Ưu tiên giữ lại 'page' và 'size' từ Thymeleaf)
	                if (!url.searchParams.has(key)) {
	                    url.searchParams.set(key, value);
	                }
	            });

	            // 4. Cập nhật lại giá trị href mới cho thẻ <a>
	            link.href = url.pathname + url.search;
	        } catch (e) {
	            console.error("Error updating pagination link:", e);
	        }
	    });
	});
	
})()