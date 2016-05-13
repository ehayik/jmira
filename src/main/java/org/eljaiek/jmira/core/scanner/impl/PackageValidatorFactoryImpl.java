
package org.eljaiek.jmira.core.scanner.impl;

import org.eljaiek.jmira.core.scanner.PackageValidator;
import org.eljaiek.jmira.core.scanner.PackageValidatorFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Lazy
@Component
final class PackageValidatorFactoryImpl implements PackageValidatorFactory, ApplicationContextAware {

     private ApplicationContext context;
    
    @Override
    public PackageValidator getPackageValidator(boolean checksum) {
        Class validatorClass = checksum ? ChecksumPackageValidator.class : LengthPackageValidator.class;
        return (PackageValidator) context.getBean(validatorClass);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }    
}
