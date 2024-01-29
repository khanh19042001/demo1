package com.MobileFolk.report;

import com.MobileFolk.EmailConfig.Email;
import com.MobileFolk.EmailConfig.EmailUtil;
import com.MobileFolk.constants.FrameworkConst;
import com.MobileFolk.utils.Log;
import org.testng.*;
import org.testng.collections.Lists;
import org.testng.internal.Utils;
import org.testng.log4testng.Logger;
import org.testng.xml.XmlSuite;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static org.testng.ITestResult.*;

public class EmailableReporter implements IReporter {

    private static final Logger LOG = Logger.getLogger(EmailableReporter.class);
    protected PrintWriter writer;
    protected final List<SuiteResult> suiteResults = Lists.newArrayList();
    private final StringBuilder buffer = new StringBuilder();
    private String dReportFileName = "emailable_report.html";

    @Override
    public void generateReport(List<XmlSuite> xmlSuites, List<ISuite> suites, String outputDirectory) {
        try {
            writer = createWriter("ExtentReports");
        } catch (IOException e) {
            LOG.error("Unable to create output file", e);
        }
        for (ISuite suite : suites) {
            suiteResults.add(new SuiteResult(suite));
        }

        writeDocumentStart();
        writeHead();
        writeBody();
        writeDocumentEnd();


        writer.close();

    }


    protected PrintWriter createWriter(String outDir) throws IOException {
        File file = new File(outDir);
        if (!file.exists()) file.mkdirs();
        OutputStream os = Files.newOutputStream(Paths.get(outDir + File.separator + dReportFileName));
        return new PrintWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8));
    }

    protected void writeReportTitle() {
        String s = "<p>Gửi các thành viên dự án ELEGANT, <br><br>Automation Team gửi báo cáo kết quả thực thi dự án " + FrameworkConst.PROJECT_NAME + ", phiên bản <b> version_build</b><br></p>" +
                "<p></p> <p>Build result: <b>build_result</b><br></p>" +
                "<p> Report Links: <br></p> <p>&emsp;<a href=\"ExtentReportLink\">- Extent Report Link </a>" +
                "<br></p> <p>&emsp;<a href=\"AlureReportLink\">- Allure Report Link </a> </p>" +
                "<p><i>(Đây là mail phát hành tự động sau khi thực thi. Vui lòng liên hệ Automation Team nếu bạn cần thêm thông tin!)</i>.</p>";
        writer.println(s);
        writer.println("<p>=================================================</p><br><h4>Report Detail:</h4>");
    }

    protected void writeDocumentStart() {
        writer.println("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.1//EN\" \"http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd\">");
        writer.println("<html xmlns=\"http://www.w3.org/1999/xhtml\">");
    }

    protected void writeHead() {
        writer.println("<head>");
        writer.println("<meta http-equiv=\"content-type\" content=\"text/html; charset=UTF-8\"/>");
        writer.println(String.format("<title>%s</title>", FrameworkConst.REPORT_TITLE));
        writeStylesheet();
        writer.println("</head>");
    }

    protected void writeStylesheet() {
        writer.println("<link rel=\"stylesheet\" href=\"https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/css/bootstrap.min.css\">");
        writer.print("<style type=\"text/css\">");
        writer.print("table {margin-bottom:10px;border-collapse:collapse;empty-cells:show}");
        writer.print("#summary {margin-top:30px}");
        writer.print("h1 {font-size:30px}");
        writer.print("body {width:100%;}");
        writer.print(".alignLeft {text-align: left;}");
        writer.print("th,td {padding: 10px; border-style: none ridge ridge none;}");
        writer.print("th {vertical-align:top;background-color:#33CC33}");
        writer.print("td {vertical-align:top}");
        writer.print("table a {font-weight:bold;color:#0D1EB6;}");
        writer.print(".easy-overview {margin-left: auto; margin-right: auto;} ");
        writer.print(".easy-test-overview tr:first-child {background-color:#D3D3D3}");
        writer.print(".stripe td {background-color: #E6EBF9}");
        writer.print(".num {text-align:right}");
        writer.print(".numbold {text-align:right;font-weight: bold;}");
        writer.print(".passed {color: #33CC33;font-weight: bold}");
        writer.print(".skipped {color: #DDD;font-weight: bold}");
        writer.print(".failed {color: #F33;font-weight: bold}");
        writer.print(".stacktrace {font-family:monospace}");
        writer.print(".totop {font-size:85%;text-align:center;border-bottom:2px solid #000}");
        writer.print(".invisible {display:none}");
        writer.println("</style>");
    }

    protected void writeBody() {
        writer.println("<body>");
        writeReportTitle();
        writeSuiteSummary();
        writeScenarioSummary();
        writer.println("</body>");
    }

    protected void writeDocumentEnd() {
        writer.println("</html>");
    }

    protected void writeSuiteSummary() {
        NumberFormat integerFormat = NumberFormat.getIntegerInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm:ss a");

        int totalTestsCount = 0;
        int totalPassedTests = 0;
        int totalSkippedTests = 0;
        int totalFailedTests = 0;
        long totalDuration = 0;

        writer.println("<div class=\"easy-test-overview\">");
        writer.println("<table class=\"table-bordered\">");
        writer.print("<tr>");
        writer.print("<th>Test/Class</th>");
        writer.print("<th>No. Test</th>");
        writer.print("<th>Passed</th>");
        writer.print("<th>Failed</th>");
        writer.print("<th>Skipped</th>");
        writer.print("<th>Start Time</th>");
        writer.print("<th>End Time</th>");
        writer.print("<th>Total Time</th>");
        writer.println("</tr>");

        int testIndex = 0;
        for (SuiteResult suiteResult : suiteResults) {
            writer.print("<tr><th colspan=\"8\" class=\"alignLeft\">");
            writer.print(Utils.escapeHtml("Test suite: " + suiteResult.getSuiteName()));
            writer.println("</th></tr>");

            for (TestResult testResult : suiteResult.getTestResults()) {
                int testsCount = testResult.getTestCount();
                int passedTests = testResult.getPassedTestCount();
                int skippedTests = testResult.getSkippedTestCount();
                int failedTests = testResult.getFailedTestCount();

                Date startTime = testResult.getTestStartTime();
                Date endTime = testResult.getTestEndTime();
                long duration = testResult.getDuration();

                writer.print("<tr");
                if ((testIndex % 2) == 1) {
                    writer.print(" class=\"stripe\"");
                }
                writer.print(">");

                buffer.setLength(0);
                writeTableData(buffer.append("<strong>")
                        .append(Utils.escapeHtml("Test: " + testResult.getTestName()))
                        .append("</strong>").toString());
                writeTableData(integerFormat.format(testsCount), "numbold");
                writeTableData(integerFormat.format(passedTests), "numbold");
                writeTableData(integerFormat.format(failedTests), "numbold");
                writeTableData(integerFormat.format(skippedTests), "numbold");
                writeTableData(dateFormat.format(startTime), "numbold");
                writeTableData(dateFormat.format(endTime), "numbold");
                writeTableData(convertTimeToString(duration), "numbold");
                writer.println("</tr>");

                totalTestsCount += testsCount;
                totalPassedTests += passedTests;
                totalSkippedTests += skippedTests;
                totalFailedTests += failedTests;
                totalDuration += duration;
                testIndex++;

                List<ClassResult> classResults = testResult.getAllClassTestResults();
                for (ClassResult classResult : classResults) {
                    buffer.setLength(0);
                    String className = classResult.getClassName();
                    writeTableData(buffer.append(Utils.escapeHtml(className.substring(className.lastIndexOf(".") + 1))).toString());
                    writeTableData(integerFormat.format(classResult.getMethodResults().size()), "num");
                    writeTableData(integerFormat.format(classResult.nPassTC), "num");
                    writeTableData(integerFormat.format(classResult.nFailTC), "num");
                    writeTableData(integerFormat.format(classResult.nSkipTC), "num");
                    writeTableData(dateFormat.format(classResult.start), "num");
                    writeTableData(dateFormat.format(classResult.end), "num");
                    writeTableData(convertTimeToString(classResult.end - classResult.start), "num");
                    writer.println("</tr>");
                }

            }
        }

        // Print totals if there was more than one test
        if (testIndex > 1) {
            writer.print("<tr>");
            writer.print("<th>Total</th>");
            writeTableHeader(integerFormat.format(totalTestsCount), "num");
            writeTableHeader(integerFormat.format(totalPassedTests), "num");
            writeTableHeader(integerFormat.format(totalFailedTests), "num");
            writeTableHeader(integerFormat.format(totalSkippedTests), "num");
            writer.print("<th colspan=\"2\"></th>");
            writeTableHeader(convertTimeToString(totalDuration), "num");
            writer.println("</tr>");
        }

        writer.println("</table>");
        writer.println("</div>");
    }

    /**
     * Writes a summary of all the test scenarios.
     */
    protected void writeScenarioSummary() {
        writer.println("<br><h4>List FAIL and SKIP testcases:</h4>");
        writer.print("<div class=\"easy-test-summary\">");
        writer.print("<table class=\"table-bordered\" id='summary'>");
        writer.print("<thead>");
        writer.print("<tr>");
        writer.print("<th>Class</th>");
        writer.print("<th>Method</th>");
        writer.print("<th>Description</th>");
        writer.print("<th>Start Time</th>");
        writer.print("<th>End Time</th>");
        writer.print("<th>Duration</th>");
        writer.print("<th>Test Result</th>");
        writer.print("</tr>");
        writer.print("</thead>");

        int testIndex = 0;
        int scenarioIndex = 0;
        for (SuiteResult suiteResult : suiteResults) {
            /*
            writer.print("<tbody><tr><th colspan=\"7\" class=\"alignLeft\">");
            writer.print(Utils.escapeHtml("Test Suite: " + suiteResult.getSuiteName()));
            writer.print("</th></tr></tbody>");
            */

            for (TestResult testResult : suiteResult.getTestResults()) {
                writer.printf("<tbody id=\"t%d\" class=\"alignLeft\">", testIndex);

                String testName = Utils.escapeHtml(testResult.getTestName());
                int startIndex = scenarioIndex;

                scenarioIndex += writeScenarioSummary(testName, testResult.getAllClassTestResults(), scenarioIndex);

                if (scenarioIndex == startIndex) {
                    writer.print("<tr><th colspan=\"4\" class=\"invisible\"/></tr>");
                }

                writer.println("</tbody>");
                testIndex++;
            }
        }

        writer.println("</table>");
        writer.println("</div>");
    }

    /**
     * Writes the scenario summary for the results of a given state for a single
     * test.
     */
    private int writeScenarioSummary(String description, List<ClassResult> classResults, int startingScenarioIndex) {
        int scenarioCount = 0;
        if (!classResults.isEmpty()) {
            /*
            writer.print("<tr><th colspan=\"7\">Test class: ");
            writer.print(description);
            writer.print("</th></tr>");
             */

            int scenarioIndex = startingScenarioIndex;
            for (ClassResult classResult : classResults) {
                buffer.setLength(0);
                int scenariosPerClass = 0;
                boolean isFirst = true;
                if (classResult.nSkipTC == 0 && classResult.nFailTC == 0)
                    continue;

                for (MethodResult methodResult : classResult.getMethodResults()) {
                    List<ITestResult> results = methodResult.getResults();
                    int resultsCount = results.size();
                    String methodName = Utils.escapeHtml(methodResult.methodName);
                    for (ITestResult result : results) {
                        String testResult = "";
                        if (result.getStatus() == SUCCESS) {
                            testResult = "passed";
                            resultsCount--;
                            continue;
                        }

                        if (result.getStatus() == FAILURE)
                            testResult = "failed";
                        else if (result.getStatus() == SKIP)
                            testResult = "skipped";

                        DateFormat formatter = new SimpleDateFormat("hh:mm:ss a");
                        Calendar startTime = Calendar.getInstance();
                        startTime.setTimeInMillis(result.getStartMillis());

                        Calendar endTime = Calendar.getInstance();
                        endTime.setTimeInMillis(result.getEndMillis());

                        if (!isFirst) {
                            buffer.append("<tr>");
                            isFirst = false;
                        }
                        String desc = result.getAttributeNames().contains("description") ? (String) result.getAttribute("description") : result.getMethod().getDescription();

                        buffer.append("<td>").append(methodName)
                                .append("</td><td rowspan=\"\">").append(desc)
                                .append("</td><td rowspan=\"\">").append(formatter.format(startTime.getTime()))
                                .append("</td><td rowspan=\"\">").append(formatter.format(endTime.getTime()))
                                .append("</td><td rowspan=\"\">").append(convertTimeToString(result.getEndMillis() - result.getStartMillis()))
                                .append("</td><td rowspan=\"\" class=\"")
                                .append(testResult).append("\">")
                                .append(testResult.toUpperCase())
                                .append("</td></tr>");
                        scenarioIndex++;

                    }
                    scenariosPerClass += resultsCount;
                }

                // Write the test results for the class
                writer.print("<tr>");
                writer.print("<td rowspan=\"");
                writer.print(scenariosPerClass);
                writer.print("\"><strong>");
                String className = classResult.getClassName();
                writer.print(Utils.escapeHtml(className.substring(className.lastIndexOf(".") + 1)));
                writer.print("</strong></td>");
                writer.print(buffer);
            }
            scenarioCount = scenarioIndex - startingScenarioIndex;
        }
        return scenarioCount;
    }

    /**
     * Writes a TH element with the specified contents and CSS class names.
     *
     * @param html       the HTML contents
     * @param cssClasses the space-delimited CSS classes or null if there are no
     *                   classes to apply
     */
    protected void writeTableHeader(String html, String cssClasses) {
        writeTag("th", html, cssClasses);
    }

    /**
     * Writes a TD element with the specified contents.
     *
     * @param html the HTML contents
     */
    protected void writeTableData(String html) {
        writeTableData(html, null);
    }

    /**
     * Writes a TD element with the specified contents and CSS class names.
     *
     * @param html       the HTML contents
     * @param cssClasses the space-delimited CSS classes or null if there are no
     *                   classes to apply
     */
    protected void writeTableData(String html, String cssClasses) {
        writeTag("td", html, cssClasses);
    }

    /**
     * Writes an arbitrary HTML element with the specified contents and CSS
     * class names.
     *
     * @param tag        the tag name
     * @param html       the HTML contents
     * @param cssClasses the space-delimited CSS classes or null if there are no
     *                   classes to apply
     */
    protected void writeTag(String tag, String html, String cssClasses) {
        writer.print("<");
        writer.print(tag);
        if (cssClasses != null) {
            writer.print(" class=\"");
            writer.print(cssClasses);
            writer.print("\"");
        }
        writer.print(">");
        writer.print(html);
        writer.print("</");
        writer.print(tag);
        writer.print(">");
    }

    /**
     * Groups {@link TestResult}s by suite.
     */
    protected static class SuiteResult {
        private final String suiteName;
        private final List<TestResult> testResults = Lists.newArrayList();

        public SuiteResult(ISuite suite) {
            suiteName = suite.getName();
            for (ISuiteResult suiteResult : suite.getResults().values()) {
                testResults.add(new TestResult(suiteResult.getTestContext()));
            }
        }

        public String getSuiteName() {
            return suiteName;
        }

        /**
         * @return the test results (possibly empty)
         */
        public List<TestResult> getTestResults() {
            return testResults;
        }
    }

    /**
     * Groups {@link ClassResult}s by test, type (configuration or test), and
     * status.
     */
    protected static class TestResult {
        /**
         * Orders test results by class name and then by method name (in
         * lexicographic order).
         */
        protected static final Comparator<ITestResult> RESULT_COMPARATOR = new Comparator<ITestResult>() {
            @Override
            public int compare(ITestResult o1, ITestResult o2) {
                int result = o1.getTestClass().getName()
                        .compareTo(o2.getTestClass().getName());
                if (result == 0) {
                    result = o1.getMethod().getMethodName()
                            .compareTo(o2.getMethod().getMethodName());
                }
                return result;
            }
        };

        protected static final Comparator<MethodResult> METHODRESULT_COMPARATOR = new Comparator<MethodResult>() {
            @Override
            public int compare(MethodResult o1, MethodResult o2) {
                return o1.methodName
                        .compareTo(o2.methodName);
            }
        };

        private final String testName;
        private final Date testStartTime;
        private final Date testEndTime;
        private final List<ClassResult> failedConfigurationResults;
        private final List<ClassResult> failedTestResults;
        private final List<ClassResult> skippedConfigurationResults;
        private final List<ClassResult> skippedTestResults;
        private final List<ClassResult> passedTestResults;
        private List<ClassResult> allClassTestResults;
        private final int failedTestCount;
        private final int skippedTestCount;
        private final int passedTestCount;
        private final int testCount;
        private final long duration;

        public TestResult(ITestContext context) {
            testName = context.getName();

            Set<ITestResult> failedConfigurations = context
                    .getFailedConfigurations().getAllResults();
            Set<ITestResult> failedTests = context.getFailedTests()
                    .getAllResults();
            Set<ITestResult> skippedConfigurations = context
                    .getSkippedConfigurations().getAllResults();
            Set<ITestResult> skippedTests = context.getSkippedTests()
                    .getAllResults();
            Set<ITestResult> passedTests = context.getPassedTests()
                    .getAllResults();

            failedConfigurationResults = groupResults(failedConfigurations);
            failedTestResults = groupResults(failedTests);
            skippedConfigurationResults = groupResults(skippedConfigurations);
            skippedTestResults = groupResults(skippedTests);
            passedTestResults = groupResults(passedTests);

            testStartTime = context.getStartDate();
            testEndTime = context.getEndDate();

            failedTestCount = failedTests.size();
            skippedTestCount = skippedTests.size();
            passedTestCount = passedTests.size();
            testCount = context.getAllTestMethods().length;

            duration = context.getEndDate().getTime() - context.getStartDate().getTime();

            allClassTestResults = createClassResult();
        }

        protected List<ClassResult> createClassResult() {
            List<ClassResult> classResults = new ArrayList<>();

            for (ClassResult classResult : failedConfigurationResults) {
                if (classResults.contains(classResult)) {
                    int index = classResults.indexOf(classResult);
                    mergerMethodResult(classResults.get(index).methodResults, classResult.getMethodResults());
                    classResults.get(index).nFailTC += classResult.nFailTC;
                } else {
                    classResults.add(classResult);
                }
            }
            for (ClassResult classResult : failedTestResults) {
                if (classResults.contains(classResult)) {
                    int index = classResults.indexOf(classResult);
                    mergerMethodResult(classResults.get(index).methodResults, classResult.getMethodResults());
                    classResults.get(index).nFailTC += classResult.nFailTC;
                } else {
                    classResults.add(classResult);
                }
            }
            for (ClassResult classResult : skippedConfigurationResults) {
                if (classResults.contains(classResult)) {
                    int index = classResults.indexOf(classResult);
                    mergerMethodResult(classResults.get(index).methodResults, classResult.getMethodResults());
                    classResults.get(index).nSkipTC += classResult.nSkipTC;
                } else {
                    classResults.add(classResult);
                }
            }
            for (ClassResult classResult : skippedTestResults) {
                if (classResults.contains(classResult)) {
                    int index = classResults.indexOf(classResult);
                    mergerMethodResult(classResults.get(index).methodResults, classResult.getMethodResults());
                    classResults.get(index).nSkipTC += classResult.nSkipTC;
                } else {
                    classResults.add(classResult);
                }
            }
            for (ClassResult classResult : passedTestResults) {
                if (classResults.contains(classResult)) {
                    int index = classResults.indexOf(classResult);
                    mergerMethodResult(classResults.get(index).methodResults, classResult.getMethodResults());
                    classResults.get(index).nPassTC += classResult.nPassTC;
                } else {
                    classResults.add(classResult);
                }
            }

            for (ClassResult classResult : classResults) {
                Log.info(String.format("%s pass %d skip %d fail %d", classResult.className, classResult.nPassTC, classResult.nSkipTC, classResult.nFailTC));
                classResult.getMethodResults().sort(METHODRESULT_COMPARATOR);
            }

            return classResults;

        }

        protected void mergerMethodResult(List<MethodResult> target, List<MethodResult> source) {
            for (MethodResult methodResult : source) {
                if (target.contains(methodResult)) {
                    int index = target.indexOf(methodResult);
                    target.get(index).results.addAll(methodResult.results);
                    target.get(index).pass += methodResult.pass;
                    target.get(index).skip += methodResult.skip;
                    target.get(index).fail += methodResult.fail;
                    if (target.get(index).start > methodResult.start)
                        target.get(index).start = methodResult.start;
                    if (target.get(index).end < methodResult.end)
                        target.get(index).end = methodResult.end;
                } else {
                    target.add(methodResult);
                }
            }
        }

        protected List<ClassResult> groupResults(Set<ITestResult> results) {
            List<ClassResult> classResults = Lists.newArrayList();
            if (!results.isEmpty()) {
                List<MethodResult> resultsPerClass = Lists.newArrayList();
                List<ITestResult> resultsPerMethod = Lists.newArrayList();

                List<ITestResult> resultsList = Lists.newArrayList(results);
                resultsList.sort(RESULT_COMPARATOR);
                Iterator<ITestResult> resultsIterator = resultsList.iterator();
                assert resultsIterator.hasNext();

                ITestResult result = resultsIterator.next();
                resultsPerMethod.add(result);

                String previousClassName = result.getTestClass().getName();
                String previousMethodName = result.getMethod().getMethodName();
                String previousDescription = result.getMethod().getDescription();
                while (resultsIterator.hasNext()) {
                    result = resultsIterator.next();

                    String className = result.getTestClass().getName();
                    if (!previousClassName.equals(className)) {
                        // Different class implies different method
                        assert !resultsPerMethod.isEmpty();
                        resultsPerClass.add(new MethodResult(previousMethodName, previousDescription, resultsPerMethod));
                        resultsPerMethod = Lists.newArrayList();

                        assert !resultsPerClass.isEmpty();
                        classResults.add(new ClassResult(previousClassName,
                                resultsPerClass));
                        resultsPerClass = Lists.newArrayList();

                        previousClassName = className;
                        previousMethodName = result.getMethod().getMethodName();
                        previousDescription = result.getMethod().getDescription();
                    } else {
                        String methodName = result.getMethod().getMethodName();
                        String description = result.getMethod().getDescription();
                        if (!previousMethodName.equals(methodName)) {
                            assert !resultsPerMethod.isEmpty();
                            resultsPerClass.add(new MethodResult(previousMethodName, previousDescription, resultsPerMethod));
                            resultsPerMethod = Lists.newArrayList();

                            previousMethodName = methodName;
                            previousDescription = description;
                        }
                    }
                    resultsPerMethod.add(result);
                }
                assert !resultsPerMethod.isEmpty();
                resultsPerClass.add(new MethodResult(previousMethodName, previousDescription, resultsPerMethod));
                assert !resultsPerClass.isEmpty();
                classResults.add(new ClassResult(previousClassName,
                        resultsPerClass));
            }
            return classResults;
        }

        public String getTestName() {
            return testName;
        }

        public Date getTestStartTime() {
            return testStartTime;
        }

        public Date getTestEndTime() {
            return testEndTime;
        }

        public List<ClassResult> getAllClassTestResults() {
            return allClassTestResults;
        }

        public int getFailedTestCount() {
            return failedTestCount;
        }

        public int getSkippedTestCount() {
            return skippedTestCount;
        }

        public int getPassedTestCount() {
            return passedTestCount;
        }

        public long getDuration() {
            return duration;
        }

        public int getTestCount() {
            return testCount;
        }

    }

    /**
     * Groups {@link MethodResult}s by class.
     */
    protected static class ClassResult {
        private final String className;
        private final List<MethodResult> methodResults;
        int nPassTC = 0, nFailTC = 0, nSkipTC = 0;
        long start = Long.MAX_VALUE, end = Long.MIN_VALUE;

        public ClassResult(String className, List<MethodResult> methodResults) {
            this.className = className;
            this.methodResults = methodResults;
            for (MethodResult result : methodResults) {
                nPassTC += result.pass;
                nFailTC += result.fail;
                nSkipTC += result.skip;

                if (start > result.start)
                    start = result.start;

                if (end < result.end)
                    end = result.end;
            }
        }

        public String getClassName() {
            return className;
        }

        /**
         * @return the non-null, non-empty {@link MethodResult} list
         */
        public List<MethodResult> getMethodResults() {
            return methodResults;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof ClassResult) {
                return className.equals(((ClassResult) obj).className);
            }
            return false;
        }

    }

    /**
     * Groups test results by method.
     */
    protected static class MethodResult {
        private final List<ITestResult> results;
        private String methodName;
        private String description;
        int pass = 0, skip = 0, fail = 0;
        long start = Long.MAX_VALUE, end = Long.MIN_VALUE;

        /**
         * @param results the non-null, non-empty result list
         */
        public MethodResult(String methodName, String description, List<ITestResult> results) {
            this.results = results;
            this.methodName = methodName;
            this.description = description;
            for (ITestResult result : results) {
                switch (result.getStatus()) {
                    case SKIP:
                        skip++;
                        break;
                    case SUCCESS:
                        pass++;
                        break;
                    case FAILURE:
                        fail++;
                        break;
                }

                if (start > result.getStartMillis())
                    start = result.getStartMillis();

                if (end < result.getEndMillis())
                    end = result.getEndMillis();
            }
        }

        /**
         * @return the non-null, non-empty result list
         */
        public List<ITestResult> getResults() {
            return results;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof MethodResult) {
                return methodName.equals(((MethodResult) obj).methodName);
            }
            return false;
        }

    }

    /* Convert long type milliseconds to format hh:mm:ss */
    public String convertTimeToString(long miliSeconds) {
        int hrs = (int) TimeUnit.MILLISECONDS.toHours(miliSeconds) % 24;
        int min = (int) TimeUnit.MILLISECONDS.toMinutes(miliSeconds) % 60;
        int sec = (int) TimeUnit.MILLISECONDS.toSeconds(miliSeconds) % 60;
        return String.format("%02d:%02d:%02d", hrs, min, sec);
    }

}
