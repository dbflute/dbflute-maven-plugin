package org.dbflute.maven.plugin;

import java.util.Arrays;
import java.util.List;

import org.apache.maven.plugin.testing.AbstractMojoTestCase;

/**
 * @author jflute
 */
public class CreateClientPluginTest extends AbstractMojoTestCase {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    private static final String ENGINE_PREFIX = "dbflute-";

    // ===================================================================================
    //                                                                      Latest Version
    //                                                                      ==============
    public void test_extractLatestVersion_basic() throws Exception {
        // ## Arrange ##
        CreateClientPlugin plugin = new CreateClientPlugin();
        List<String> engineDirNameList = Arrays.asList("dbflute-1.1.0", "dbflute-1.1.1");

        // ## Act ##
        String latestVersion = plugin.extractLatestVersion(ENGINE_PREFIX, engineDirNameList);

        // ## Assert ##
        assertEquals("1.1.1", latestVersion);
    }

    public void test_extractLatestVersion_onlyone() throws Exception {
        // ## Arrange ##
        CreateClientPlugin plugin = new CreateClientPlugin();
        List<String> engineDirNameList = Arrays.asList("dbflute-1.1.0");

        // ## Act ##
        String latestVersion = plugin.extractLatestVersion(ENGINE_PREFIX, engineDirNameList);

        // ## Assert ##
        assertEquals("1.1.0", latestVersion);
    }

    public void test_extractLatestVersion_snapshot() throws Exception {
        // ## Arrange ##
        CreateClientPlugin plugin = new CreateClientPlugin();
        List<String> engineDirNameList = Arrays.asList("dbflute-1.1.0-RC1", "dbflute-1.1.0-00-SNAPSHOT", "dbflute-1.1.0");

        // ## Act ##
        String latestVersion = plugin.extractLatestVersion(ENGINE_PREFIX, engineDirNameList);

        // ## Assert ##
        assertEquals("1.1.0", latestVersion);
    }

    public void test_extractLatestVersion_servicePack_basic() throws Exception {
        // ## Arrange ##
        CreateClientPlugin plugin = new CreateClientPlugin();
        List<String> engineDirNameList = Arrays.asList("dbflute-1.1.0-sp1", "dbflute-1.1.0");

        // ## Act ##
        String latestVersion = plugin.extractLatestVersion(ENGINE_PREFIX, engineDirNameList);

        // ## Assert ##
        assertEquals("1.1.0-sp1", latestVersion);
    }

    public void test_extractLatestVersion_servicePack_eachOther() throws Exception {
        // ## Arrange ##
        CreateClientPlugin plugin = new CreateClientPlugin();
        List<String> engineDirNameList = Arrays.asList("dbflute-1.1.0-sp1", "dbflute-1.1.0-sp2");

        // ## Act ##
        String latestVersion = plugin.extractLatestVersion(ENGINE_PREFIX, engineDirNameList);

        // ## Assert ##
        assertEquals("1.1.0-sp2", latestVersion);
    }

    public void test_extractLatestVersion_servicePack_snapshot() throws Exception {
        // ## Arrange ##
        CreateClientPlugin plugin = new CreateClientPlugin();
        List<String> engineDirNameList = Arrays.asList("dbflute-1.1.0-sp1", "dbflute-1.1.0-sp2-RC1");

        // ## Act ##
        String latestVersion = plugin.extractLatestVersion(ENGINE_PREFIX, engineDirNameList);

        // ## Assert ##
        assertEquals("1.1.0-sp2-RC1", latestVersion);
    }

    public void test_extractLatestVersion_onparade() throws Exception {
        // ## Arrange ##
        CreateClientPlugin plugin = new CreateClientPlugin();
        List<String> engineDirNameList = Arrays.asList("dbflute-1.1.0" // 2
                , "dbflute-1.1.0-RC1" // 3
                , "dbflute-1.1.0-" // 6
                , "dbflute-1.0.9" // 7
                , "dbflute-1.1.0-sp1" // 1
                , "dbflute-1.1.0-01-SNAPSHOT" // 4
                , "emecha-1.1.0" // 9
                , "jflute" // 8
                , "dbflute-1.1.0-UNKNOWN" // 5
        );

        // ## Act ##
        String latestVersion = plugin.extractLatestVersion(ENGINE_PREFIX, engineDirNameList);

        // ## Assert ##
        assertEquals("1.1.0-sp1", latestVersion);
    }
}
