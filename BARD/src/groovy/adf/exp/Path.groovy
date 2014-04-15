package adf.exp

import bard.db.experiment.JsonResult

/**
 * Created by ddurkin on 3/20/14.
 */

class Path {
    ResultKey result;

    Path parent

    Path(JsonResult jr, parent) {
        this.result = new ResultKey(jr);
        this.parent = parent
    }

    List<Path> getPath() {
        if (this.parent == null) {
            return [this]
        } else {
            def path = parent.getPath()
            path.add(this)
            return path
        }
    }

    boolean equals(o) {
        if (this.is(o)) return true
        if (!(o instanceof Path)) return false

        Path that = (Path) o

        if (parent != that.parent) return false
        if (result != that.result) return false

        return true
    }

    int hashCode() {
        int result1
        result1 = (result != null ? result.hashCode() : 0)
        result1 = 31 * result1 + (parent != null ? parent.hashCode() : 0)
        return result1
    }

    String toString() {
        return result.toString()
    }
}
