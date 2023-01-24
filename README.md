**How to build**

mvn clean compile assembly:single

**How to run**

java -jar target/engine-1.0-SNAPSHOT-jar-with-dependencies.jar << src/test/resources/test1.txt


**NOTES**

- application was designed to be single-threaded
- id generation is based on System#currentTimeMillis. Do NOT use simple long counter as it won't survive application restarts
- to reduce GC load we need:
  - make Trade class reusable instead of generation each time new instance
  - replace String fields with Bytes (or other implementation which won't use heap)
  - Do the same for Order
  - Replace CsvParsing lib with something else, because current does not have a feature of getting nextField in direct byte buffer. 
  But I do suspect that in real world we would use System.in to get orders. In case of reading it from messages I would consider Chronicle Wire.
- To improve complexity of order adding/cancelling we might consider replacing TreeSets with arrays of linkedLists but this would need thorough examination.

  