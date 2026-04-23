(() => {
    'use strict'

    // Fetch all the forms we want to apply custom Bootstrap validation styles to
    const forms = document.querySelectorAll('.needs-validation')

    // Loop over them and prevent submission
    Array.from(forms).forEach(form => {
        form.addEventListener('submit', event => {
            if (!form.checkValidity()) {
                event.preventDefault()
                event.stopPropagation()
            }

            form.classList.add('was-validated')
        }, false)
    });

    // Xử lý với modal load content bởi ajax
    document.querySelectorAll('.modal').forEach(modal => {
        console.log("EVENT")
        modal.addEventListener('show.bs.modal', (event) => {
            console.log("SHOW")
            let button = event.relatedTarget;

            // Button modal phải có data-ajax-url
            if (!button.dataset.ajaxUrl) return;

            let url = button.dataset.ajaxUrl;
            let modalBody = modal.querySelector('.modal-body');
            modalBody.innerHTML = `
			        <div class="text-center p-3">
			            <div class="spinner-border text-primary"></div>
			            <p>Đang tải dữ liệu...</p>
			        </div>`;

            fetch(url).then((resp) => {
                if (!resp.ok) throw new Error('Không thể kết nối đến máy chủ');
                return resp.text();
            }). then((html) => {
                modalBody.innerHTML = html;
            });
        });
    });

    let deleteModal = document.getElementById('deleteModal');
    if (deleteModal) {
        deleteModal.addEventListener('show.bs.modal', function (event) {
            console.log(event.relatedTarget)
            // Thẻ <a> kích hoạt modal
            let anchor = event.relatedTarget;

            // Trích xuất thông tin
            let deleteUrl = anchor.getAttribute('href');
            let contactName = anchor.getAttribute('data-name');
            console.log(contactName);

            // Cập nhật nội dung Modal
            let modalName = deleteModal.querySelector('#delete-name');
            let modalBtnDelete = deleteModal.querySelector('#confirm-delete-link');

            modalName.textContent = contactName;
            modalBtnDelete.setAttribute('href', deleteUrl);
        });
    }

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
        maintainAspectRatio: false,
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
                    dataset.tension = 0.4; // Đường cong mượt
                    dataset.pointRadius = 4;
                    dataset.pointHoverRadius = 6;
                    dataset.borderWidth = 3;
                }
                else if (type === 'bar') {
                    dataset.backgroundColor = theme.border; // Cột dùng màu đậm
                    dataset.hoverBackgroundColor = theme.border.replace('1)', '0.8)');
                    dataset.borderRadius = 6; // Bo góc đầu cột chuyên nghiệp
                    dataset.borderSkipped = false;
                }
                else if (['pie', 'doughnut'].includes(type)) {
                    dataset.backgroundColor = PROFESSIONAL_PALETTE.map(p => p.border);
                    dataset.hoverOffset = 15;
                    dataset.borderWidth = 2;
                    dataset.borderColor = '#ffffff';
                    if (type === 'doughnut') {
                        dynamicOptions.cutout = '75%'; // Làm vòng khuyên mỏng và hiện đại
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
})()
