package biz.isphere.junit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import biz.isphere.core.clcommands.CLTokenizer;

public class TestCLTokenizer {

    @Test
    public void testGood() throws Exception {

        CLTokenizer tokenizer = new CLTokenizer();

        validateCommand(tokenizer, new String[] { "CRTCLPGM", "PGM(LIB/PGM)", "SRCFILE(SRCLIB/SRCFILE)", "SRCMBR(*PGM)",
            "TEXT('My first ''Hello World'' program')", "OPTION((*XREF) *GEN)" });

        validateCommand(tokenizer, new String[] { "CRTCLPGM", "LIB/PGM", "SRCLIB/SRCFILE", "*PGM", "'My first ''Hello World'' program'",
            "((*XREF) *GEN)" });

        validateCommand(tokenizer, new String[] { "ADDBNDDIRE", "BNDDIR(LIB/BNDDIR)", "OBJ((OBJLIB/OBJ *SRVPGM *DEFER))" });

        validateCommand(tokenizer, new String[] { "ADDBNDDIRE", "BNDDIRLIB/BNDDIR", "((OBJLIB/OBJ *SRVPGM *DEFER))" });
    }

    @Test
    public void testBadCheckErrors() throws Exception {

        CLTokenizer tokenizer = new CLTokenizer();

        try {
            // single quote is missing, causing parameter 4+5 to be joined
            tokenizer.tokenizeCommand(buildCommand(new String[] { "CRTCLPGM", "PGM(LIB/PGM)", "SRCFILE(SRCLIB/SRCFILE)", "SRCMBR(*PGM)",
                "TEXT('My first ''Hello World' program')", "OPTION((*XREF) *GEN)" }));
            fail("Exception not seen.");
        } catch (Exception e) {
            String message = e.getLocalizedMessage();
            assertEquals("Exception mesages to not match", "Error parsing command string. End character ['] not seen.", message);
        }

        try {
            // closing bracket missing: "((*XREF *GEN)"
            tokenizer.tokenizeCommand(buildCommand(new String[] { "CRTCLPGM", "LIB/PGM", "SRCLIB/SRCFILE", "*PGM",
                "'My first ''Hello World'' program'", "((*XREF *GEN)" }));
            fail("Exception not seen.");
        } catch (Exception e) {
            String message = e.getLocalizedMessage();
            assertEquals("Exception mesages to not match", "Error parsing command string. End character [)] not seen.", message);
        }

        try {
            // single quote is missing and closing bracket missing
            tokenizer.tokenizeCommand(buildCommand(new String[] { "CRTCLPGM", "LIB/PGM", "SRCLIB/SRCFILE", "*PGM",
                "'My first ''Hello World' program'", "((*XREF *GEN)" }));
            fail("Exception not seen.");
        } catch (Exception e) {
            String message = e.getLocalizedMessage();
            assertEquals("Exception mesages to not match", "Error parsing command string. End character ['] not seen.", message);
        }

    }

    @Test
    public void testBadIgnoreErrors() throws Exception {

        CLTokenizer tokenizer = new CLTokenizer(true);

        validateCommandIgnoringErrors(tokenizer, new String[] { "CRTCLPGM", "PGM(LIB/PGM)", "SRCFILE(SRCLIB/SRCFILE)", "SRCMBR(*PGM)",
            "TEXT('My first ''Hello World' program')", "OPTION((*XREF) *GEN)" });

        validateCommandIgnoringErrors(tokenizer, new String[] { "CRTCLPGM", "LIB/PGM", "SRCLIB/SRCFILE", "*PGM",
            "'My first ''Hello World'' program'", "((*XREF *GEN)" });
    }

    private void validateCommandIgnoringErrors(CLTokenizer tokenizer, String[] expectedTokens) throws Exception {

        String expectedCommand = buildCommand(expectedTokens);

        System.out.println("Validating (ignoring errors): " + expectedCommand);

        String[] actualTokens = tokenizer.tokenizeCommand(expectedCommand);

        String actualCommand = buildCommand(actualTokens);

        assertEquals("Commands do not match", expectedCommand, actualCommand);

        Set<String> expectedTokensSet = new HashSet<String>(Arrays.asList(expectedTokens));
        for (int i = 0; i < actualTokens.length; i++) {
            if (expectedTokensSet.contains(actualTokens[i])) {
                expectedTokensSet.remove(actualTokens[i]);
                actualTokens[i] = null;
            }
        }

        /**
         * Try to match the remaining expected tokens against the remaining
         * actual tokens. Actual tokens could be a combination of expected token
         * at this time like: actual = expected_1 + " " + expected_2
         * <p>
         * That can happen on invalid commands, when a single quote is missing
         * so that or or more command parameters have been joined.
         */
        String[] remainingExpectedTokens = expectedTokensSet.toArray(new String[expectedTokensSet.size()]);
        // Sort tokens descending by length
        Arrays.sort(remainingExpectedTokens, new Comparator<String>() {
            public int compare(String arg0, String arg1) {
                if (arg0.length() > arg1.length()) {
                    return -1;
                } else if (arg0.length() < arg1.length()) {
                    return 1;
                }
                return 0;
            }
        });

        boolean hasChanged;

        do {
            hasChanged = false;
            for (int i = 0; i < remainingExpectedTokens.length; i++) {
                if (remainingExpectedTokens[i] != null) {
                    for (int j = 0; j < actualTokens.length; j++) {
                        if (actualTokens[j] != null) {
                            if (actualTokens[j].endsWith(remainingExpectedTokens[i])) {
                                actualTokens[j] = actualTokens[j].substring(0, actualTokens[j].length() - remainingExpectedTokens[i].length()).trim();
                                if (actualTokens[j].trim().length() == 0) {
                                    actualTokens[j] = null;
                                }
                                expectedTokensSet.remove(remainingExpectedTokens[i]);
                                hasChanged = true;
                            } else if (actualTokens[j].startsWith(remainingExpectedTokens[i])) {
                                actualTokens[j] = actualTokens[j].substring(remainingExpectedTokens[i].length()).trim();
                                if (actualTokens[j].trim().length() == 0) {
                                    actualTokens[j] = null;
                                }
                                expectedTokensSet.remove(remainingExpectedTokens[i]);
                                hasChanged = true;
                            }
                        }
                    }
                }
            }
        } while (hasChanged);

        for (String actualToken : actualTokens) {
            assertTrue("Actual token is not [null]: " + actualToken, actualToken == null);
        }
    }

    private void validateCommand(CLTokenizer tokenizer, String[] expectedTokens) throws Exception {

        String command = buildCommand(expectedTokens);

        System.out.println("Validating: " + command);

        String[] actualTokens = tokenizer.tokenizeCommand(command);
        validateTokens(expectedTokens, actualTokens);
    }

    private void validateTokens(String[] expectedTokens, String[] actualTokens) {

        assertTrue("Number of tokens do not match", expectedTokens.length == actualTokens.length);

        for (int i = 0; i < expectedTokens.length; i++) {
            assertEquals("Tokens do not match", expectedTokens[i], actualTokens[i]);
        }

    }

    private String buildCommand(String[] commandTokens) {

        StringBuilder buffer = new StringBuilder();

        for (String token : commandTokens) {
            if (buffer.length() >= 0) {
                buffer.append(" ");
            }
            buffer.append(token);
        }

        return buffer.toString();
    }
}
