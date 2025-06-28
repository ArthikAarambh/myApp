**api_automation**

``mvn -U clean test -DtestSuite=myApp/myAppSuite.xml``

Generate allure report
allure generate :- in the root project directory.
allure generate target/allure-results --clean -o test-report/allure-report
allure generate allure-results --clean -o test-report/allure-report

working db is note_database.db






