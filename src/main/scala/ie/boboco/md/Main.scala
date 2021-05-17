package ie.boboco.md

import java.io.{File, FileWriter, PrintWriter}
import java.text.SimpleDateFormat
import java.util.Date
import scala.io.Source
import scala.language.{implicitConversions, reflectiveCalls}
import scala.util.Try

trait Utils {
  def using[A, B <: {def close(): Unit}](closeable: B)(f: B => A): A = {
    val result = Try(f(closeable))
    closeable.close()
    result.get
  }

  object Main extends App with Utils {

    val prefix = "000 TODOs - "
    val dir = new File(args(0))
    val todos = findMarkdownFiles(dir).filter(containsUncompleted).map(getUncompleted)

    writeTodos(todos)

    def writeTodos(todos: Seq[(File, Seq[String])]): Unit = {
      val sdf = new SimpleDateFormat("yyyy-MM-dd")
      val fileName: String = s"$prefix${sdf.format(new Date())}.md"
      val destFile = new File(dir.getAbsolutePath, fileName)
      using(new PrintWriter(new FileWriter(destFile))) { writer =>
        writer.println(s"# Open TODOs")
        for (todo <- todos) {
          writer.println("\n")
          writer.println(s"## [[${todo._1.getName.replaceAll("\\.md", "")}]]")
          writer.println("\n")
          for (line <- todo._2) {
            writer.println(line)
          }
        }
      }
    }

    def findMarkdownFiles(dir: File): List[File] = {
      assert(dir.isDirectory, s"$dir is not a directory")
      val mdThisDir = dir.listFiles { pathname =>
        pathname.isFile && pathname.getName.endsWith(".md") && !pathname.getName.startsWith(prefix)
      }.toList.sortBy(_.getName)
      mdThisDir ::: dir.listFiles(_.isDirectory).toList.flatMap(d => findMarkdownFiles(d))
    }

    def containsUncompleted(f: File): Boolean = using(Source.fromFile(f)) { buffered =>
      buffered.getLines().exists(_.contains("[ ]"))
    }

    def getUncompleted(f: File): (File, Seq[String]) = using(Source.fromFile(f)) { buffered =>
      (f, buffered.getLines().filter(_.contains("[ ]")).toList)
    }
  }
