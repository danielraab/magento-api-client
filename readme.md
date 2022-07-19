# Magento API Client

#### Java 11 necessary

## Short description
* Small kotlin/swing application with gui
* preconfigure application with `config.json`


### Categories

* export categories as csv tree
* export category details as csv list
* update category details from csv list
  * necessary csv format:
    ```csv
    cat.id;cat.attributeType;cat.attrCode;cat.attrValue
    151;custom_attributes;description;"Testtest"
    151;custom_attributes;meta_title;more content to test
    151;custom_attributes;meta_description;"some more"","" tests</div>"
    ```
  * **at the moment only custom attributes are supported !!**

### Products

* export products to csv
* select and update product attribute
  * update single attribute (specified in the gui)

* query and export product attributes as csv
    * product attribute sets
    * product attributes
    * product attributes with options
    * full specified product attributes

## look and feel

TODO: add screenshots

## how to use

### compile code

* install java 11
* execute from source path
  * linux: `./gradlew fatJar`
  * windows: `./gradlew.bat fatJar` (not tested)
* jar file will be generated in `./build/libs`

### execute jar

* set execute rights for `.jar` files
* execute file: `java -jar {jar_file}`
