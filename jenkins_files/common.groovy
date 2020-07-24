#!/usr/bin/env groovy

def withNvmVer(Closure body) {
    withNvm("v10.14.2", "npmrcFile") {
        withYvm("v1.15.2") {
            body()
        }
    }
}

return this
