package com.queryio.demo.extended.metadata;

public class Main {
	// public static void main(String[] args) throws Exception {
	// BasicConfigurator.configure();
	// final String fileName =
	// "/work/tika-site/tika-parsers/src/test/resources/test-documents/testWORD_1img.doc";
	//// final String fileName =
	// "/work/tika-site/tika-parsers/src/test/resources/test-documents/testEXCEL_1img.xlsx";
	//
	// System.out.println(new File(fileName).length());
	//
	// final FileContentParser parser = new FileContentParser();
	//
	// // Without Pipes
	// System.out.println("Without Pipes");
	// parser.parseStream(new FileInputStream(new File(fileName)),
	// fileName.substring(fileName.lastIndexOf(".") + 1));
	//
	// parser.getCustomTagList().clear();
	//
	// // With Pipes
	// System.out.println("\nWith Pipes");
	// PipedOutputStream pos = new PipedOutputStream();
	// final PipedInputStream pis = new PipedInputStream(pos);
	// Thread t = new Thread(){
	// public void run(){
	// try{
	// parser.parseStream(pis, fileName.substring(fileName.lastIndexOf(".") +
	// 1));
	// }catch(Exception e){
	// e.printStackTrace();
	// }
	// }
	// };
	// t.start();
	//
	// FileInputStream fis = new FileInputStream(new File(fileName));
	// byte[] buffer = new byte[1024];
	//
	// int bytesIn = 0;
	// while ((bytesIn = fis.read(buffer, 0, buffer.length)) != -1) {
	// pos.write(buffer, 0, bytesIn);
	// }
	//
	// fis.close();
	// pos.close();
	// t.join();
	// pis.close();
	// }
}
