package test.sl;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import sl.StandardHeaderGenerator;
import sl.StandardHeaderGeneratorException;

public class StandardHeaderGeneratorTestSuite {
    ClassLoader classloader;
    URL copyright;
    StandardHeaderGenerator generator;
    URL javaProps;
    URL cppProps;
    URL cProps;
    URL bashProps;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
        classloader = ClassLoader.getSystemClassLoader();
        copyright = classloader.getResource("Copyright.txt");

        javaProps = classloader.getResource("java.header.properties");
        cppProps = classloader.getResource("cpp.header.properties");
        cProps = classloader.getResource("c.header.properties");
        bashProps = classloader.getResource("bash.header.properties");
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public final void testAddHeader() {
        try {
            URL src = classloader.getResource("Foo.java");
            generator = new StandardHeaderGenerator(javaProps);
            generator.addHeader(copyright, src);
        } catch (StandardHeaderGeneratorException e) {
            fail("Unexpected exception:" + e.getMessage());
        }
    }

}
