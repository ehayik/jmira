package org.eljaiek.jmira.data.repositories.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.ToLongFunction;
import java.util.stream.Collectors;
import org.eljaiek.jmira.core.util.ValidationUtils;
import org.eljaiek.jmira.data.model.DebPackage;
import org.eljaiek.jmira.data.repositories.PackageRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author eljaiek
 */
//@Repository
final class MemoryPackageRepository implements PackageRepository {

    private static final Predicate<DebPackage> VALID_PACKAGE = p -> ValidationUtils.isValidFile(p.getLocalUrl(), p.getChecksum());

    private static final Predicate<DebPackage> INVALID_PACKAGE = p -> !ValidationUtils.isValidFile(p.getLocalUrl(), p.getChecksum());

    private static final ToLongFunction<DebPackage> TO_SIZE = p -> p.getSize();

    private final List<DebPackage> packages;

    public MemoryPackageRepository() {
        packages = new ArrayList<>();
    }

    @Override
    public void saveAll(List<DebPackage> packages) {
        this.packages.addAll(packages);
    }

    @Override
    public List<DebPackage> findAll(int start, int limit) {

        if (packages.isEmpty()) {
            return Collections.EMPTY_LIST;
        }

        int limitIndex = start + limit - 1;
        return packages.subList(start, limitIndex);
    }

    @Override
    public List<DebPackage> findNotDownloaded() {
        return packages.parallelStream()
                .filter(INVALID_PACKAGE)
                .collect(Collectors.toList());
    }

    @Override
    public void removeAll() {
        packages.clear();
    }

    @Override
    public int count() {
        return packages.size();
    }

    @Override
    public int countDownloaded() {
        return (int) packages.parallelStream()
                .filter(VALID_PACKAGE)
                .count();
    }

    @Override
    public long size() {
        return packages.parallelStream().mapToLong(TO_SIZE).sum();
    }

    @Override
    public long downloaded() {
        return packages.parallelStream()
                .filter(VALID_PACKAGE)
                .mapToLong(TO_SIZE)
                .sum();
    }
}
