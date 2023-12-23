package com.refinedmods.refinedsites.render;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.regex.Pattern;

import org.asciidoctor.ast.Document;
import org.asciidoctor.extension.IncludeProcessor;
import org.asciidoctor.extension.PreprocessorReader;

class IncludeProcessorImpl extends IncludeProcessor {
    private static final Pattern XREF_PATTERN = Pattern.compile("xref:(.*)");

    @Override
    public boolean handles(final String target) {
        return true;
    }

    @Override
    public void process(final Document document,
                        final PreprocessorReader reader,
                        final String target,
                        final Map<String, Object> attributes) {
        final Path path = Paths.get(reader.getDir()).resolve(target);
        try {
            final String content = Files.readString(path);
            // ensure that included files can xref to other files, from the standpoint of the included file path
            // (and not from the standpoint of the includer)
            final String fixedContent = XREF_PATTERN.matcher(content).replaceAll(matchResult -> {
                final String xrefPath = matchResult.group(1);
                final Path xrefPathFull = path.getParent().resolve(xrefPath);
                final Path relativeToSource = Paths.get(reader.getDir()).relativize(xrefPathFull);
                return "xref:" + relativeToSource;
            });
            reader.pushInclude(fixedContent, path.toString(), path.toString(), 1, attributes);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }
}
