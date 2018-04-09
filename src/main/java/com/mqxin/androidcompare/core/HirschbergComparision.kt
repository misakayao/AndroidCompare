package com.mqxin.androidcompare.core

class HirschbergComparision {

    fun hirschbergAlgorithm(original: StringDescription, comparing: StringDescription): String {
        if (comparing.content == null || comparing.end < comparing.begin || original.end < original.begin) {
            return ""
        } else if (comparing.end == comparing.begin) {
            var s = ""

            for (i in original.begin..original.end) {
                if (original.content[i] == comparing.content[comparing.begin]) {
                    s += original.content[i]
                }


            }

            return s
        } else if (original.begin == original.end) {
            var s = ""

            for (i in comparing.begin..comparing.end) {

                if (comparing.content[i] == original.content[original.begin]) {
                    s += comparing.content[i]
                }
            }
            return s
        }


        val mid = (original.end + original.begin) / 2
        val originalEnd = original.end
        val originalBegin = original.begin
        original.end = mid
        val mcsFirstHalf = computeMCSLength(original, comparing, false)

        original.begin = mid + 1
        original.end = originalEnd
        val mcsSecondHalf = computeMCSLength(original, comparing, true)

        var maxPos = 0
        val comparingLength = comparing.end - comparing.begin + 1
        var maxVal = 0
        for (j in 0 until comparingLength) {
            if (mcsFirstHalf[j] + mcsSecondHalf[comparingLength - j - 1] > maxVal) {
                maxPos = j
                maxVal = mcsFirstHalf[j] + mcsSecondHalf[comparingLength - j - 1]
            }
        }


        val comparingEnd = comparing.end
        val comparingBegin = comparing.begin

        original.begin = originalBegin
        original.end = mid
        comparing.end = comparing.begin + maxPos
        val first = hirschbergAlgorithm(original, comparing)

        original.begin = mid + 1
        original.end = originalEnd
        comparing.begin = comparingBegin + maxPos + 1
        comparing.end = comparingEnd
        val second = hirschbergAlgorithm(original, comparing)

        return first + second

    }

    private fun computeMCSLength(original: StringDescription, comparing: StringDescription, reverse: Boolean): IntArray {
        val comparingContentLength = comparing.end - comparing.begin + 1
        val originalContentLength = original.end - original.begin + 1

        val mcsLength = Array(2) { IntArray(comparingContentLength) }

        for (i in 0 until comparingContentLength) {
            mcsLength[1][i] = 0
        }

        if (original.end < original.begin || comparing.end < comparing.begin) {
            return mcsLength[1]
        }

        var strOriginal = original.content.substring(original.begin, original.end + 1)
        var strComparing = comparing.content.substring(comparing.begin, comparing.end + 1)
        if (reverse) {
            strOriginal = StringBuilder(strOriginal).reverse().toString()
            strComparing = StringBuilder(strComparing).reverse().toString()
        }

        for (i in 0 until originalContentLength) {
            for (k in 0 until comparingContentLength) {
                mcsLength[0][k] = mcsLength[1][k]
            }

            for (j in 0 until comparingContentLength) {
                if (strOriginal[i] == strComparing[j]) {
                    mcsLength[1][j] = if (j > 0) mcsLength[0][j - 1] + 1 else 1
                } else {
                    if (j > 0) {
                        mcsLength[1][j] = Math.max(mcsLength[1][j - 1], mcsLength[0][j])
                    } else {
                        mcsLength[1][j] = 0
                    }

                }
            }
        }

        return mcsLength[1]
    }
}
