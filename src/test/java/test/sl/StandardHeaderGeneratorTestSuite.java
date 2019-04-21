package test.sl;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import sl.StandardHeaderGenerator;
import sl.StandardHeaderGeneratorException;

public class StandardHeaderGeneratorTestSuite {
    private ClassLoader classloader;
    private URL copyrightUrl;
    private StandardHeaderGenerator headerGenerator;
    
    // Header comments format by language 
    private URL javaProps;
    private URL cppProps;
    private URL cProps;
    private URL bashProps;

    @Before
    public void setUp() throws Exception {
        classloader = ClassLoader.getSystemClassLoader();
        copyrightUrl = classloader.getResource("Copyright.txt");

        // Header comments format by language 
        javaProps = classloader.getResource("java.header.properties");
        cppProps = classloader.getResource("cpp.header.properties");
        cProps = classloader.getResource("c.header.properties");
        bashProps = classloader.getResource("bash.header.properties");
    }

    @Test
    public final void testAddHeaderInJava() {
        // Tests Java language
        addHeader("Foo.java", javaProps);
    }
    
    @Test
    public final void testAddHeaderInCpp() {
        // Tests C++ language
        addHeader("Foo.cpp", cppProps);
    }
    
    @Test
    public final void testAddHeaderInC() {
        // Tests C language
        addHeader("Foo.c", cProps);
    }
    
    @Test
    public final void testAddHeaderInBash() {
        // Tests bash script
        addHeader("Foo.sh", bashProps);
    }

    /**
     * Concrete test to add copyright as header to source file.
     * @param srcFile source file name
     * @param langProps header comment properties by language
     */
    private void addHeader(String srcFile, URL langProps) {
        // Backs up original source file
        String chkFile = srcFile+".chk";
        backupFile(srcFile);
        
        // Adds header to source file
        try {
            URL src = classloader.getResource(srcFile);
            headerGenerator = new StandardHeaderGenerator(langProps);
            headerGenerator.addHeader(copyrightUrl, src);
        } catch (StandardHeaderGeneratorException e) {
            fail("Unexpected exception:" + e.getMessage());
        }
        
        // Validates the generated file is identical as expected.
        assertTrue(fileEquals(srcFile, chkFile));
        
        // Restores source file from backup for repeatable tests
        restoreFile(srcFile);
        
        // Deletes backup file
        deleteBakFile(srcFile);
    }

    /**
     * Checks if two files are identical.
     * @param file1
     * @param file2
     * @return true if equals to each other
     */
    private boolean fileEquals(String file1, String file2) {
        try {
            byte[] f1 = Files.readAllBytes(Paths.get(classloader.getResource(file1).toURI()));
            byte[] f2 = Files.readAllBytes(Paths.get(classloader.getResource(file2).toURI()));
            return Arrays.equals(f1, f2);
        } catch (IOException | URISyntaxException e) {
            return false;
        }
     }
    
    /**
     * Backs up source file with extension .bak, replaces it if exists.
     * @param file name of source file
     */
    private void backupFile(String file) {
        try {
            URI original = classloader.getResource(file).toURI();
            URI backup = Paths.get(original.getPath()+".bak").toUri();

            Files.copy(Paths.get(original), 
                       Paths.get(backup),
                       StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();;
        }
    }
    
    /**
     * Copies backup to original source file, replaces it if exists.
     * @param file name of source file
     */
    private void restoreFile(String file) {
        try {
            URI backup = classloader.getResource(file+".bak").toURI();
            URI original = classloader.getResource(file).toURI();
            
            Files.copy(Paths.get(backup), 
                       Paths.get(original),
                       StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException | URISyntaxException e) {
            // Ignores exception handling in test
            e.printStackTrace();
        }
    }
    
    /**
     * Deletes backup file.
     * @param file name of source file
     */
    private void deleteBakFile(String file) {
        try {
            URI backup = classloader.getResource(file+".bak").toURI();
            Files.delete(Paths.get(backup));
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();;
        }
    }
}
