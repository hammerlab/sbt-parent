
import org.scalatest.FunSuite

class Test
  extends FunSuite {
  if (System.getenv("TRAVIS") == null)
    assert(false, "This module's tests should not be run!")
}
