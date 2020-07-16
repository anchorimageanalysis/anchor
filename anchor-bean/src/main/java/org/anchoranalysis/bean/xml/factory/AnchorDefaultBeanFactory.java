/* (C)2020 */
package org.anchoranalysis.bean.xml.factory;

import java.nio.file.Path;
import java.nio.file.Paths;
import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.bean.BeanInstanceMap;
import org.anchoranalysis.bean.xml.error.BeanMisconfiguredXmlException;
import org.anchoranalysis.bean.xml.error.BeanXmlException;
import org.anchoranalysis.bean.xml.error.HelperFriendlyExceptions;
import org.apache.commons.configuration.ConfigurationRuntimeException;
import org.apache.commons.configuration.beanutils.BeanDeclaration;
import org.apache.commons.configuration.beanutils.BeanFactory;
import org.apache.commons.configuration.beanutils.XMLBeanDeclaration;

/**
 * The default bean factory used for initializing AnchorBeans
 *
 * <p>i.e. the factory used when no config-factory attribute is set in the XML
 *
 * @author Owen Feehan
 */
public class AnchorDefaultBeanFactory implements BeanFactory {

    private BeanInstanceMap defaultInstances;

    public AnchorDefaultBeanFactory(BeanInstanceMap defaultInstances) {
        super();
        this.defaultInstances = defaultInstances;
    }

    @Override
    public Object createBean(Class<?> beanClass, BeanDeclaration data, Object parameter)
            throws Exception {
        try {
            Object result = createBeanInstance(beanClass, data);
            initBeanInstance(result, data, parameter);

            // We check initParams and localise if it's a bean
            if (result instanceof AnchorBean) {
                AnchorBean<?> resultCast = (AnchorBean<?>) result;
                resultCast.checkMisconfigured(defaultInstances);

                Path localPath = Paths.get((String) parameter);
                resultCast.localise(localPath);
            }

            return result;
        } catch (ConfigurationRuntimeException e) {
            // We suppress these exceptions as they are ugly to read, and instead
            //   focus on where in the XML file the error is occurring
            if (data instanceof XMLBeanDeclaration) {
                throw createMisconfiguredBeanException(e, (XMLBeanDeclaration) data);
            }

            String msg =
                    String.format(
                            "A misconfigured bean (%s) exists at unknown location",
                            beanClass.getName());
            throw new BeanXmlException(msg, e.getCause());
        }
    }

    private static BeanMisconfiguredXmlException createMisconfiguredBeanException(
            ConfigurationRuntimeException exc, XMLBeanDeclaration dataCast) {

        // We can read the ClassType from beanClass.getName() but we don't report this to
        //  the user, as we assume it is presented later as part of the trace of the XML
        //  element by DescribeXMLNodeUtilities.describeXMLNode

        String msg =
                String.format(
                        "A misconfigured bean exists%n%s",
                        HelperDescribeXmlNode.describeXMLNode(dataCast.getNode()));
        return new BeanMisconfiguredXmlException(msg, maybeRepaceException(exc));
    }

    /**
     * In certain cases we display a more simplified or alternative exception as the cause
     *
     * @param exc exception to consider replacing
     * @return either the same exception passed in or a meaingful replacement
     */
    private static Throwable maybeRepaceException(ConfigurationRuntimeException exc) {

        if (isListMissingFactory(exc.getCause())) {
            return new BeanXmlException(
                    "A list declaration in BeanXML is missing its factory. Please add config-factory=\"list\"");
        }

        return HelperFriendlyExceptions.maybeCreateUserFriendlyException(exc);
    }

    private static boolean isListMissingFactory(Throwable exc) {
        return (exc instanceof NoSuchMethodException
                && exc.getMessage().equals("java.util.List.<init>()"));
    }

    /** Returns null as {@Optional} is not supported by {@link BeanFactory} interface. */
    @Override
    public Class<?> getDefaultBeanClass() {
        return null;
    }

    protected Object createBeanInstance(Class<?> beanClass, BeanDeclaration data)
            throws Exception // NOSONAR
            {
        return beanClass.getConstructor().newInstance();
    }

    protected void initBeanInstance(Object bean, BeanDeclaration data, Object parameter)
            throws Exception // NOSONAR
            {
        DefaultBeanFactoryHelperInit.initBean(bean, data, parameter);
    }

    public BeanInstanceMap getDefaultInstances() {
        return defaultInstances;
    }
}
