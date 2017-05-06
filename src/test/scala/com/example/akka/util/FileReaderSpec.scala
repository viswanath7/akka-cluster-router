package com.example.akka.util

import org.scalatest.FlatSpec

class FileReaderSpec extends FlatSpec {

  "Supplying a valid classpath path file name" should "return the lines in a file" in {
    assert(FileReader.readClasspathFile("quotes.txt").size == 20)
  }

  "Supplying an invalid classpath path file name" should "return the lines in a file" in {
    assert(FileReader.readClasspathFile("unknown.txt").isEmpty)
  }

}
