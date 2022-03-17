package magentoAPIClient


fun String.quote() = "\"${this.replace("\"", "\"\"")}\""