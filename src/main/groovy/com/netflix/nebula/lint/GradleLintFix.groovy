package com.netflix.nebula.lint

import groovy.transform.Canonical

/**
 * Used to generate a unified diff format of auto-corrections for violations
 */
abstract class GradleLintFix {
    File affectedFile

    abstract int from()
    abstract int to()
    abstract String changes()
}

abstract class GradleLintMultilineFix extends GradleLintFix {
    Range<Integer> affectedLines // 1-based, inclusive

    @Override
    int from() { affectedLines.from }

    @Override
    int to() { affectedLines.to }
}

@Canonical
class GradleLintReplaceWith extends GradleLintMultilineFix {
    Integer fromColumn // the first affected column of the first line (1-based, inclusive)
    Integer toColumn // the last affected column of the last line (1-based, exclusive)
    String changes

    GradleLintReplaceWith(File affectedFile, Range<Integer> affectedLines, Integer fromColumn, Integer toColumn, String changes) {
        this.affectedFile = affectedFile
        this.affectedLines = affectedLines
        this.fromColumn = fromColumn
        this.toColumn = toColumn
        this.changes = changes
    }

    @Override
    String changes() { changes }
}

class GradleLintDelete extends GradleLintMultilineFix {

    GradleLintDelete(File affectedFile, Range<Integer> affectedLines) {
        this.affectedFile = affectedFile
        this.affectedLines = affectedLines
    }

    @Override
    String changes() { null }
}

class GradleLintInsertAfter extends GradleLintFix {
    Integer afterLine // 1-based
    String changes

    public GradleLintInsertAfter(File affectedFile, Integer afterLine, String changes) {
        this.affectedFile = affectedFile
        this.afterLine = afterLine
        this.changes = changes
    }

    @Override
    int from() { afterLine+1 }

    @Override
    int to() { afterLine }

    @Override
    String changes() { changes }
}

@Canonical
class GradleLintInsertBefore extends GradleLintFix {
    Integer beforeLine // 1-based
    String changes

    public GradleLintInsertBefore(File affectedFile, Integer beforeLine, String changes) {
        this.affectedFile = affectedFile
        this.beforeLine = beforeLine
        this.changes = changes
    }

    @Override
    int from() { beforeLine }

    @Override
    int to() { beforeLine-1 }

    @Override
    String changes() { changes }
}