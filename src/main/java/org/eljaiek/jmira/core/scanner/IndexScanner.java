package org.eljaiek.jmira.core.scanner;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;
import org.eljaiek.jmira.core.model.Architecture;

/**
 *
 * @author eduardo.eljaiek
 */
@FunctionalInterface
public interface IndexScanner {

    List<String> scan(Context context) throws InvalidIndexFileException;

    public final class Context {

        private static final String LAST_BAR_REGEX = "\\|$";

        private static final String WORDS_LIST_REGEX = "^.*?(%s).*$";

        private final String archsRegex;

        private final String componentsRegex;

        private final String relaseFile;

        public Context(String releaseFile, Architecture[] archs, String components) {
            this.relaseFile = releaseFile;
            StringBuilder builder = new StringBuilder();

            for (Architecture arch : archs) {
                builder.append(arch.getFolder());
                builder.append("|");
            }

            String folders = builder.toString().replaceFirst(LAST_BAR_REGEX, "");
            this.archsRegex = String.format(WORDS_LIST_REGEX, folders);

            String comps = components.trim().replaceAll(" ", "|");
            this.componentsRegex = String.format(WORDS_LIST_REGEX, comps);
        }

        public String getArchsRegex() {
            return archsRegex;
        }

        public String getComponentsRegex() {
            return componentsRegex;
        }

        public Stream<String> getIndexLines() throws IOException {
            try {
                return Files.lines(Paths.get(new URI(relaseFile)));
            } catch (URISyntaxException ex) {
                throw new IOException(ex);
            }
        }
    }
}
