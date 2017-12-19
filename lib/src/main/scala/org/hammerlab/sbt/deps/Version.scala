package org.hammerlab.sbt.deps

trait SnapshotOps {
  implicit val SnapshotOps = Snapshot.SnapshotOps _
  implicit val DefaultVersionOps = Snapshot.DefaultVersionOps _
}

object Snapshot {
  val suffix = "-SNAPSHOT"
  implicit class SnapshotOps(val s: String) extends AnyVal {
    def isSnapshot: Boolean = s.endsWith(suffix)
    def snapshot: String = if (!isSnapshot) s"$s$suffix" else s
    def snapshot(isSnapshot: Boolean): String =
      (isSnapshot, this.isSnapshot) match {
        case (true, false) ⇒ s.snapshot
        case (false, true) ⇒ s.dropRight(suffix.length)
        case _ ⇒ s
      }
    def maybeForceSnapshot(forceSnapshot: Boolean): String =
      if (forceSnapshot)
        snapshot
      else
        s
  }

  implicit class DefaultVersionOps(val t: (Dep, String)) extends AnyVal {
    def snapshot: (Dep, String) = (t._1, t._2.snapshot)
  }
}

object VersionOps extends SnapshotOps
