package org.eljaiek.jmira.core.io.impl;

import org.eljaiek.jmira.core.model.DebPackage;
import org.eljaiek.jmira.core.io.DataAccessException;
import org.eljaiek.jmira.core.io.PackageRepository;
import org.eljaiek.jmira.core.io.PackagesFileProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.eljaiek.jmira.core.scanner.PackageValidator;
import org.eljaiek.jmira.core.scanner.PackageValidatorFactory;

@Repository
final class FilePackageRepositoryImpl implements PackageRepository {

    @Autowired
    private PackagesFileProvider provider;

    @Autowired
    private PackageValidatorFactory validatorFactory;

    @Override
    public void saveAll(List<DebPackage> packages) {
        Assert.isTrue(provider.getFile().isPresent());

        try (RandomAccessFile raf = new RandomAccessFile(provider.getFile().get(), "rw")) {
            int count = packages.size();
            long size = packages.stream()
                    .collect(Collectors.summingLong(p -> p.getLength()));

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

        if (!provider.getFile().get().exists()) {
            return Collections.EMPTY_LIST;
        }

        try (RandomAccessFile raf = new RandomAccessFile(provider.getFile().get(), "r")) {
            List<DebPackage> result = new ArrayList<>(limit);
            int size = 0;
            int seeked = 0;
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
    public final long downloadsSize(boolean checksum) {
        PackageValidator validator = validatorFactory.getPackageValidator(checksum);
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

                if (validator.validate(pkg)) {
                    downloaded += pkg.getLength();
                }
            }

            return downloaded;
        } catch (IOException | ClassNotFoundException ex) {
            throw new DataAccessException(ex.getMessage(), ex);
        }
    }

    @Override
    public List<DebPackage> findIdles(boolean checksum) {
        PackageValidator validator = validatorFactory.getPackageValidator(checksum);
        Assert.isTrue(provider.getFile().isPresent());

        if (!provider.getFile().get().exists()) {
            return Collections.EMPTY_LIST;
        }

        try (RandomAccessFile raf = new RandomAccessFile(provider.getFile().get(), "r")) {
            List<DebPackage> result = new ArrayList<>(0);
            raf.readInt();
            raf.readLong();

            while (raf.getFilePointer() != raf.length()) {
                int length = raf.readInt();
                byte b[] = new byte[length];
                raf.read(b);
                DebPackage pkg = (DebPackage) toObject(b);

                if (!validator.validate(pkg)) {
                    result.add(pkg);
                }
            }

            return result;
        } catch (IOException | ClassNotFoundException ex) {
            throw new DataAccessException(ex.getMessage(), ex);
        }
    }

    @Override
    public int downloads(boolean checksum) {
        PackageValidator validator = validatorFactory.getPackageValidator(checksum);
        Assert.isTrue(provider.getFile().isPresent());
        int count = 0;

        if (!provider.getFile().get().exists()) {
            return count;
        }

        try (RandomAccessFile raf = new RandomAccessFile(provider.getFile().get(), "r")) {
            raf.readInt();
            raf.readLong();

            while (raf.getFilePointer() != raf.length()) {
                int length = raf.readInt();
                byte b[] = new byte[length];
                raf.read(b);
                DebPackage pkg = (DebPackage) toObject(b);

                if (validator.validate(pkg)) {
                    count++;
                }
            }

            return count;
        } catch (IOException | ClassNotFoundException ex) {
            throw new DataAccessException(ex);
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
