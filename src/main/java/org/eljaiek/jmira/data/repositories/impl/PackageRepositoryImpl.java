package org.eljaiek.jmira.data.repositories.impl;

import com.google.common.collect.Lists;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import org.eljaiek.jmira.data.model.DebPackage;
import org.eljaiek.jmira.data.repositories.DataAccessException;
import org.eljaiek.jmira.data.repositories.PackageRepository;
import org.eljaiek.jmira.data.repositories.PackagesFileProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.eljaiek.jmira.core.util.ValidationUtils;
import org.springframework.util.Assert;

@Repository
final class PackageRepositoryImpl implements PackageRepository {

    @Autowired
    private PackagesFileProvider provider;

    @Override
    public void saveAll(List<DebPackage> packages) {
        Assert.isTrue(provider.getFile().isPresent());

        try (RandomAccessFile raf = new RandomAccessFile(provider.getFile().get(), "rw")) {
            int count = packages.size();
            long size = packages.stream()
                    .collect(Collectors.summingLong(p -> p.getSize()));

            if (raf.length() != 0) {
                count += raf.readInt();
                size += raf.readLong();
            }

            raf.seek(0);
            raf.writeInt(count);
            raf.writeLong(size);
            raf.seek(raf.length());
            packages.forEach(p -> {
                try {
                    save(raf, p);
                } catch (IOException ex) {
                    throw new DataAccessException(ex.getMessage(), ex);
                }
            });

        } catch (IOException ex) {
            throw new DataAccessException(ex.getMessage(), ex);
        }
    }

    @Override
    public List<DebPackage> findAll(int start, int limit) {
        Assert.isTrue(provider.getFile().isPresent());     
        Assert.isTrue(start > 0 && limit > 0 && limit > start);
        
        if (!provider.getFile().get().exists()) {
            return Lists.newArrayList();
        }

        try (RandomAccessFile raf = new RandomAccessFile(provider.getFile().get(), "r")) {
            List<DebPackage> result = new ArrayList<>(limit);
            int size = 0;
            int seeked = 1;
            raf.readInt();
            raf.readLong();

            while (raf.getFilePointer() != raf.length() && size != limit) {
                int length = raf.readInt();

                if (seeked != start) {
                    raf.skipBytes(length);
                    seeked++;
                } else {
                    byte b[] = new byte[length];
                    raf.read(b);
                    DebPackage pkg = (DebPackage) toObject(b);
                    result.add(pkg);
                    size++;
                }
            }

            return result;
        } catch (IOException | ClassNotFoundException ex) {
            throw new DataAccessException(ex.getMessage(), ex);
        }
    }

    @Override
    public final int count() {
        Assert.isTrue(provider.getFile().isPresent());

        if (!provider.getFile().get().exists()) {
            return 0;
        }

        try (RandomAccessFile raf = new RandomAccessFile(provider.getFile().get(), "r")) {
            return raf.readInt();
        } catch (IOException ex) {
            throw new DataAccessException(ex.getMessage(), ex);
        }
    }

    @Override
    public final long size() {
        Assert.isTrue(provider.getFile().isPresent());

        if (!provider.getFile().get().exists()) {
            return 0;
        }

        try (RandomAccessFile raf = new RandomAccessFile(provider.getFile().get(), "r")) {
            raf.readInt();
            return raf.readLong();
        } catch (IOException ex) {
            throw new DataAccessException(ex.getMessage(), ex);
        }
    }

    @Override
    public final long downloaded() {
        Assert.isTrue(provider.getFile().isPresent());

        if (!provider.getFile().get().exists()) {
            return 0;
        }

        try (RandomAccessFile raf = new RandomAccessFile(provider.getFile().get(), "r")) {
            long downloaded = 0;
            raf.readInt();
            raf.readLong();

            while (raf.getFilePointer() != raf.length()) {
                int length = raf.readInt();
                byte b[] = new byte[length];
                raf.read(b);
                DebPackage pkg = (DebPackage) toObject(b);

                if (ValidationUtils.isValidFile(pkg.getLocalUrl(), null)) {
                    downloaded += pkg.getSize();
                }
            }

            return downloaded;
        } catch (IOException | ClassNotFoundException ex) {
            throw new DataAccessException(ex.getMessage(), ex);
        }
    }

    @Override
    public List<DebPackage> findNotDownloaded() {
        Assert.isTrue(provider.getFile().isPresent());

        if (!provider.getFile().get().exists()) {
            return Lists.newArrayList();
        }

        try (RandomAccessFile raf = new RandomAccessFile(provider.getFile().get(), "r")) {
            int count = raf.readInt();
            List<DebPackage> result = new ArrayList<>(0);
            raf.readLong();

            while (raf.getFilePointer() != raf.length()) {
                int length = raf.readInt();
                byte b[] = new byte[length];
                raf.read(b);
                DebPackage pkg = (DebPackage) toObject(b);
                File file = new File(pkg.getLocalUrl());

                if (!file.exists() || pkg.getSize() != file.length()) {
                    result.add(pkg);
                }
            }

            return result;
        } catch (IOException | ClassNotFoundException ex) {
            throw new DataAccessException(ex.getMessage(), ex);
        }
    }

    @Override
    public void removeAll() {
        Assert.isTrue(provider.getFile().isPresent());
        provider.getFile().get().delete();
    }

    private static void save(RandomAccessFile raf, DebPackage pkg) throws IOException {
        byte[] b = toBytes(pkg);
        raf.writeInt(b.length);
        raf.write(b);
    }

    private static byte[] toBytes(Object object) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(object);
        return baos.toByteArray();
    }

    private static Object toObject(byte[] bytes) throws IOException,
            ClassNotFoundException {
        Object object = new ObjectInputStream(new ByteArrayInputStream(bytes))
                .readObject();
        return object;
    }
}
