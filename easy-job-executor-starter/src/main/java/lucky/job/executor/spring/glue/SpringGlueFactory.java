package lucky.job.executor.spring.glue;

import groovy.lang.GroovyClassLoader;
import lucky.job.core.handler.IJobHandler;
import lucky.job.executor.spring.EasyJobSpringExecutor;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.annotation.AnnotationUtils;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author: liujun
 * @Date: 2021/1/13 16:06
 * @Description:
 */
public class SpringGlueFactory {
    private static Logger logger = LoggerFactory.getLogger(SpringGlueFactory.class);

    private static SpringGlueFactory glueFactory = new SpringGlueFactory();
    /**
     * groovy class loader
     */
    private GroovyClassLoader groovyClassLoader = new GroovyClassLoader();
    private ConcurrentMap<String, Class<?>> classCache = new ConcurrentHashMap<>();

    public static SpringGlueFactory getInstance() {
        return glueFactory;
    }

    /**
     * load new instance, prototype
     *
     * @param codeSource
     * @return
     * @throws Exception
     */
    public IJobHandler loadNewInstance(String codeSource) throws Exception {
        if (codeSource != null && codeSource.trim().length() > 0) {
            Class<?> clazz = getCodeSourceClass(codeSource);
            if (clazz != null) {
                Object instance = clazz.newInstance();
                if (instance != null) {
                    if (instance instanceof IJobHandler) {
                        this.injectService(instance);
                        return (IJobHandler) instance;
                    } else {
                        throw new IllegalArgumentException(">>>>>>>>>>> xxl-glue, loadNewInstance error, "
                                + "cannot convert from instance[" + instance.getClass() + "] to IJobHandler");
                    }
                }
            }
        }
        throw new IllegalArgumentException(">>>>>>>>>>> xxl-glue, loadNewInstance error, instance is null");
    }

    private Class<?> getCodeSourceClass(String codeSource) {
        try {
            // md5
            byte[] md5 = MessageDigest.getInstance("MD5").digest(codeSource.getBytes());
            String md5Str = new BigInteger(1, md5).toString(16);

            Class<?> clazz = classCache.get(md5Str);
            if (clazz == null) {
                clazz = groovyClassLoader.parseClass(codeSource);
                classCache.putIfAbsent(md5Str, clazz);
            }
            return clazz;
        } catch (Exception e) {
            return groovyClassLoader.parseClass(codeSource);
        }
    }

    /**
     * inject action of spring
     *
     * @param instance
     */
    public void injectService(Object instance) {
        if (instance == null) {
            return;
        }

        if (EasyJobSpringExecutor.getApplicationContext() == null) {
            return;
        }

        Field[] fields = instance.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (Modifier.isStatic(field.getModifiers())) {
                continue;
            }

            Object fieldBean = null;
            // with bean-id, bean could be found by both @Resource and @Autowired, or bean could only be found by @Autowired

            if (AnnotationUtils.getAnnotation(field, Resource.class) != null) {
                try {
                    Resource resource = AnnotationUtils.getAnnotation(field, Resource.class);
                    if (resource != null && StringUtils.isBlank(resource.name())) {
                        fieldBean = EasyJobSpringExecutor.getApplicationContext().getBean(resource.name());
                    } else {
                        fieldBean = EasyJobSpringExecutor.getApplicationContext().getBean(field.getName());
                    }
                } catch (Exception e) {
                }
                if (fieldBean == null) {
                    fieldBean = EasyJobSpringExecutor.getApplicationContext().getBean(field.getType());
                }
            } else if (AnnotationUtils.getAnnotation(field, Autowired.class) != null) {
                Qualifier qualifier = AnnotationUtils.getAnnotation(field, Qualifier.class);
                if (qualifier != null && qualifier.value().length() > 0) {
                    fieldBean = EasyJobSpringExecutor.getApplicationContext().getBean(qualifier.value());
                } else {
                    fieldBean = EasyJobSpringExecutor.getApplicationContext().getBean(field.getType());
                }
            }

            if (fieldBean != null) {
                field.setAccessible(true);
                try {
                    field.set(instance, fieldBean);
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
    }
}
