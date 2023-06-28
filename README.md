# Encode-Decode-Huffman

This is a file Encoding and Decoding program based on the huffman compression algorithm.

The following are the specs of compression:

1) EmptyTester (No text):

    EmptyTester.txt Filesize = 0 bytes
   
    EmptyTester_compressed.txt Filesize = 0 bytes
2)	Single (Only 1 character):
   
    Single.txt Filesize = 1 byte

    Single_compressed.txt Filesize = 2 bytes
3)	RepeatedLetter (1 character repeated a bunch of times):
   
    RepeatedLetter.txt Filesize = 11 bytes
  	
    RepeatedLetter_compressed.txt Filesize = 3 bytes
4)	Hello (Small File):
   
    Hello.txt Filesize = 52 bytes
  	
    Hello_compressed.txt Filesize = 28 bytes
5)	WarAndPeace (Given File):
    
    WarAndPeace.txt Filesize = 3,223,372 bytes
  	
    WarAndPeace_compressed.txt Filesize = 1,811,745 bytes
  	
Note:- The files went from using disk space of 3.2 MB to 2.2 MB which is a significant change.

6)	USConstitution (Given File):
  
    USConstitution.txt Filesize = 45,119 bytes
    
    USConstitution_compressed.txt Filesize = 25,337 bytes
  
Note:- The file size almost halves itself after compression
