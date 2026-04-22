package hocjava.util;

import java.beans.PropertyDescriptor;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

public class MapperUtil {
	/*
     * Clone object (tạo instance mới)
     */
    public static <T> T clone(Object source, Class<T> targetClass, String... ignoreProperties) {
        if (source == null) return null;

        T target = instantiate(targetClass);
        BeanUtils.copyProperties(source, target, ignoreProperties);
        return target;
    }

    /*
     * Copy từ source sang target đã tồn tại
     */
    public static void copy(Object source, Object target, String... ignoreProperties) {
        if (source == null || target == null) return;

        BeanUtils.copyProperties(source, target, ignoreProperties);
    }

    /*
     * Copy bỏ qua field null
     */
    public static void copyIgnoreNull(Object source, Object target) {
        if (source == null || target == null) return;

        BeanUtils.copyProperties(source, target, getNullPropertyNames(source));
    }

    /*
     * ===== PRIVATE =====
     */

    private static <T> T instantiate(Class<T> clazz) {
        try {
            return clazz.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Cannot instantiate: " + clazz.getName(), e);
        }
    }

    private static String[] getNullPropertyNames(Object source) {
        BeanWrapper src = new BeanWrapperImpl(source);
        PropertyDescriptor[] pds = src.getPropertyDescriptors();

        Set<String> nullProps = new HashSet<>();

        for (PropertyDescriptor pd : pds) {
            Object value = src.getPropertyValue(pd.getName());
            if (value == null) nullProps.add(pd.getName());
        }

        return nullProps.toArray(new String[0]);
    }

}
