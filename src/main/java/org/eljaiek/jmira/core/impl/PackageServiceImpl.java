package org.eljaiek.jmira.core.impl;

import java.util.List;
import org.eljaiek.jmira.core.PackageService;
import org.eljaiek.jmira.data.model.DebPackage;
import org.eljaiek.jmira.data.repositories.PackageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Service
public class PackageServiceImpl implements PackageService {

    @Autowired
    private PackageRepository packages;

    @Override
    public List<DebPackage> list(int start, int limit) {
        Assert.isTrue(start > 0);
        Assert.isTrue(limit > 0);
        return packages.findAll(start, limit);
    }

    @Override
    public long size() {
        return packages.size();
    }

    @Override
    public long downloaded() {
        return packages.downloaded();
    }

    @Override
    public List<DebPackage> getNotDownPack(int packSize) {
        Assert.isTrue(packSize > 0);
        return packages.findNotDownByLimit(packSize);
    }
}
