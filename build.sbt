name := "dialog-gen"

scalaVersion := "3.2.2"

libraryDependencies += "com.lihaoyi" %% "utest" % "0.7.10" % "test"

testFrameworks += new TestFramework("utest.runner.Framework")