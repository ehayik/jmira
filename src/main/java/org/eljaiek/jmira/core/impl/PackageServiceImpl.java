package org.eljaiek.jmira.core.impl;

import org.eljaiek.jmira.core.PackageService;
import org.eljaiek.jmira.data.model.DebPackage;
import org.eljaiek.jmira.data.repositories.PackageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;

@Service
public class PackageServiceImpl implements PackageService {

    @Autowired
    private PackageRepository packages;

    @Override
    public List<DebPackage> list(int start, int limit) {
        Assert.isTrue(start >= 0);
        Assert.isTrue(limit > 0);
        return packages.findAll(start, limit);
    }

    @Override
    public List<DebPackage> listNotDownloaded() {
        return packages.findNotDownloaded();
    }
}
