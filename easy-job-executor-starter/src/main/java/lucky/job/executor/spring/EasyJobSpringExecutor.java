package lucky.job.executor.spring;

import lucky.job.core.exception.EasyJobException;
import lucky.job.core.handler.impl.MethodJobHandler;
import lucky.job.core.model.ReturnT;
import lucky.job.executor.annotation.EasyJob;
import lucky.job.executor.excutor.EasyJobExecutor;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.annotation.AnnotatedElementUtils;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * @author: luckylau
 * @Date: 2021/1/12 15:26
 * @Description:
 */
public class EasyJobSpringExecutor extends EasyJobExecutor implements ApplicationContextAware, SmartInitializingSingleton, DisposableBean {

    private static final Logger logger = LoggerFactory.getLogger(EasyJobSpringExecutor.class);

    private static ApplicationContext applicationContext;

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        EasyJobSpringExecutor.applicationContext = applicationContext;
    }

    @Override
    public void afterSingletonsInstantiated() {
        initJobHandlerMethodRepository(applicationContext);
        try {
            super.start();
        } catch (Exception e) {
            throw new EasyJobException(e.getMessage());
        }


    }

    private void initJobHandlerMethodRepository(ApplicationContext applicationContext) {
        if (applicationContext == null) {
            return;
        }
        // init job handler from method
        String[] beanDefinitionNames = applicationContext.getBeanNamesForType(Object.class, false, true);
        for (String beanDefinitionName : beanDefinitionNames) {
            Object bean = applicationContext.getBean(beanDefinitionName);

            Map<Method, EasyJob> annotatedMethods = null;
            try {
                annotatedMethods = MethodIntrospector.selectMethods(bean.getClass(),
                        (MethodIntrospector.MetadataLookup<EasyJob>) method -> AnnotatedElementUtils.findMergedAnnotation(method, EasyJob.class));
            } catch (Throwable ex) {
                logger.error("easy-job method-job handler resolve error for bean[" + beanDefinitionName + "].", ex);
            }
            if (annotatedMethods == null || annotatedMethods.isEmpty()) {
                continue;
            }

            for (Map.Entry<Method, EasyJob> methodEasyJobEntry : annotatedMethods.entrySet()) {
                Method method = methodEasyJobEntry.getKey();
                EasyJob easyJob = methodEasyJobEntry.getValue();
                if (easyJob == null) {
                    continue;
                }

                String name = easyJob.value();
                if (StringUtils.isBlank(name)) {
                    throw new EasyJobException("easy-job method-job handler name invalid, for[" + bean.getClass() + "#" + method.getName() + "] .");
                }
                if (loadJobHandler(name) != null) {
                    throw new EasyJobException("easy-job job handler[" + name + "] naming conflicts.");
                }

                // execute method
                if (!(method.getParameterTypes().length == 1 && method.getParameterTypes()[0].isAssignableFrom(String.class))) {
                    throw new EasyJobException("easy-job method-job handler param-class type invalid, for[" + bean.getClass() + "#" + method.getName() + "] , " +
                            "The correct method format like \" public ReturnT<String> execute(String param) \" .");
                }
                if (!method.getReturnType().isAssignableFrom(ReturnT.class)) {
                    throw new EasyJobException("easy-job method-job handler return-class type invalid, for[" + bean.getClass() + "#" + method.getName() + "] , " +
                            "The correct method format like \" public ReturnT<String> execute(String param) \" .");
                }
                method.setAccessible(true);

                // init and destory
                Method initMethod = null;
                Method destroyMethod = null;

                if (!StringUtils.isBlank(easyJob.init())) {
                    try {
                        initMethod = bean.getClass().getDeclaredMethod(easyJob.init());
                        initMethod.setAccessible(true);
                    } catch (NoSuchMethodException e) {
                        throw new RuntimeException("easy-job method-job handler initMethod invalid, for[" + bean.getClass() + "#" + method.getName() + "] .");
                    }
                }
                if (!StringUtils.isBlank(easyJob.destroy())) {
                    try {
                        destroyMethod = bean.getClass().getDeclaredMethod(easyJob.destroy());
                        destroyMethod.setAccessible(true);
                    } catch (NoSuchMethodException e) {
                        throw new RuntimeException("easy-job method-job handler destroyMethod invalid, for[" + bean.getClass() + "#" + method.getName() + "] .");
                    }
                }

                // registry jobhandler
                registJobHandler(name, new MethodJobHandler(bean, method, initMethod, destroyMethod));
            }
        }

    }

}
