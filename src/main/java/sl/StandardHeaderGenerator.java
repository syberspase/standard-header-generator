package sl;

import java.io.IOException;
import java.io.StringReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Given a few lines of legal copyright text as input, program a solution for
 * prepending it as a commented header to various source code files (support C,
 * C++, and shell script syntax). Write this software as if a dozen hypothetical
 * team members world-wide are expecting to contribute changes in the future, so
 * it should be reusable for them to add other enhancements to without needing
 * oversight or involvement from you.
 * 
 * Copyright is copied from
 * https://raw.githubusercontent.com/InsightSoftwareConsortium/ITK/master/Modules/ThirdParty/KWSys/src/KWSys/Copyright.txt
 * 
 * @author Shep Liu(syberspase@gmail.com)
 *
 */
public class StandardHeaderGenerator {
    private String headerStart;
    private String lineStarts;
    private String headerEnd;

    /**
     * Constructor with language header properties file URL.
     * @param langHeaderPropsFileUrl
     * @throws StandardHeaderGeneratorException
     */
    public StandardHeaderGenerator(URL langHeaderPropsFileUrl) throws StandardHeaderGeneratorException {
        try {
            loadProps(new String(Files.readAllBytes(Paths.get(langHeaderPropsFileUrl.toURI()))));
        } catch (IOException | URISyntaxException e) {
            throw new StandardHeaderGeneratorException(e);
        }
    }

    /**
     * Constructor with string from language header properties file.
     * @param langHeaderPropsString
     * @throws StandardHeaderGeneratorException
     */
    public StandardHeaderGenerator(String langHeaderPropsString) throws StandardHeaderGeneratorException {
        loadProps(langHeaderPropsString);
    }

    /**
     * Adds header to source file.
     * @param headerUrl
     * @param srcUrl
     * @throws StandardHeaderGeneratorException
     */
    public void addHeader(URL headerUrl, URL srcUrl) throws StandardHeaderGeneratorException {
        try (Stream<String> src = Files.lines(Paths.get(srcUrl.toURI()));
             Stream<String> header = Files.lines(Paths.get(headerUrl.toURI()))) {
            StringBuilder sb = new StringBuilder();
            sb.append(headerStart).append(System.lineSeparator());
            header.forEach((line) -> {
                sb.append(lineStarts + line + System.lineSeparator());
            });
            sb.append(headerEnd).append(System.lineSeparator());

            Stream<String> newContent = Stream.concat(Stream.of(sb.toString()), src);

            List<String> replaced = newContent.collect(Collectors.toList());
            Files.write(Paths.get(srcUrl.toURI()), replaced);
            newContent.close();
        } catch (IOException | URISyntaxException e) {
            throw new StandardHeaderGeneratorException(e);
        }
    }

    /**
     * Loads properties file and set values to object fields.
     * @param langHeaderPropsString
     * @throws StandardHeaderGeneratorException
     */
    private void loadProps(String langHeaderPropsString) throws StandardHeaderGeneratorException {
        try {
            Properties props = new Properties();
            props.load(new StringReader(langHeaderPropsString));
            headerStart = props.getProperty("header.start");
            lineStarts = props.getProperty("line.starts");
            headerEnd = props.getProperty("header.end");
        } catch (IOException e) {
            throw new StandardHeaderGeneratorException(e);
        }

    }

    /**
     * For local test.
     * @param args
     * @throws StandardHeaderGeneratorException
     */
    public static void main(String[] args) throws StandardHeaderGeneratorException {
        try {
            URL src = Paths.get("./Foo.java").toUri().toURL();
            URL copyright = Paths.get("./Copyright.txt").toUri().toURL();
            URL javaProps = Paths.get("src/main/resources/java.header.properties").toUri().toURL();
            StandardHeaderGenerator generator = new StandardHeaderGenerator(javaProps);
            generator.addHeader(copyright, src);
        } catch (IOException e) {
            throw new StandardHeaderGeneratorException(e);
        }
    }
}
