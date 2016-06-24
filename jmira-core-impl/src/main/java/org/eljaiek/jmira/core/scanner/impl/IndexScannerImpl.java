package org.eljaiek.jmira.core.scanner.impl;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import org.eljaiek.jmira.core.scanner.IndexScanner;
import org.eljaiek.jmira.core.scanner.InvalidIndexFileException;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Lazy
@Component
final class IndexScannerImpl implements IndexScanner {

    private static final String SPACE_REGEX = " +";

    private static final String MD5_REGEX = "^[a-f0-9]{32}$";

    @Override
    public final List<String> scan(Context context) throws InvalidIndexFileException {
        try {
            return context
                    .getIndexLines()
                    .filter(line -> findIndices(line, context))
                    .map(line -> line.split(SPACE_REGEX)[3])
                    .collect(Collectors.toList());
        } catch (IOException ex) {
            throw new InvalidIndexFileException(ex);
        }
    }

    private boolean findIndices(String line, Context context) {

        if (line.trim().isEmpty()) {
            return false;
        }

        String[] split = line.split(SPACE_REGEX);

        if (split.length < 3) {
            return false;
        }

        String md5sum = split[1];
        String index = split[3];

        if (!md5sum.trim().matches(MD5_REGEX)) {
            return false;
        }

        if (index.matches(context.getComponentsRegex())) {
            return false;
        }

        return !(index.contains("binary") && !line.matches(context.getArchsRegex()));
    }
}
