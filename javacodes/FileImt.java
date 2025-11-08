import java.io.*;
import java.util.Scanner;

/**
 * 提供文件相关的辅助方法：定位 data 目录路径、从测试文件运行测试并写入结果
 */
public class FileImt {

	/**
	 * 根据运行目录获取正确的文件路径
	 *
	 * @param fileName 文件名
	 * @return 正确的文件路径
	 */

     
	public static String getDataFilePath(String fileName) {
		return new File("data/list_testcase.txt").exists() ? "data/" + fileName : "../data/" + fileName;
	}

	/**
	 * 比较两个文件的内容是否完全相同（按行比较，区分大小写，行尾差异视为不同）
	 * 如果任何一行不同或读取出现错误则返回 false
	 *
	 * @param path1 第一个文件路径
	 * @param path2 第二个文件路径
	 * @return 相同返回 true，否则返回 false
	 */


	public static boolean filesEqual(String path1, String path2) {
		try (BufferedReader r1 = new BufferedReader(new FileReader(path1));
			 BufferedReader r2 = new BufferedReader(new FileReader(path2))) {
			String l1, l2;
			while ((l1 = r1.readLine()) != null) {
				l2 = r2.readLine();
				if (l2 == null) return false;
				if (!l1.equals(l2)) return false;
			}
			return r2.readLine() == null;
		} catch (IOException e) {
			System.err.println("文件读写错误: " + e.getMessage());
			return false;
		}
	}

	/**
	 * 运行指定实现的测试：从 data/list_testcase.txt 读取每一行作为一个测试用例（按空格分隔命令），
	 * 调用 ListTestRunner.processCommand 执行命令，并把每个测试用例的最终结构写入结果文件。
	 *
	 * @param name 测试实现名字（用以生成结果文件名）
	 * @param list List 实现的实例
	 * @return 如果成功返回 true；读取/写入错误返回 false
	 */


	public static boolean runTests(String name, String testName, List<Character> list) {
		String inPath = getDataFilePath(testName);
		String outPath = getDataFilePath(name.replaceAll("\\s+", "") + "_result.txt");

		try (Scanner fileScanner = new Scanner(new File(inPath));
			 PrintWriter resultWriter = new PrintWriter(new FileWriter(outPath))) {

			int testCase = 1;
			while (fileScanner.hasNextLine()) {
				String line = fileScanner.nextLine().trim();
				if (line.isEmpty()) continue;

				System.out.println("测试用例 " + testCase + ": " + line.substring(0, Math.min(50, line.length())) + "...");

				// 处理命令序列（保留上一个测试用例的状态，行为与原实现一致）
				String[] commands = line.split("\\s+");
				for (String command : commands) {
					if (command.isEmpty()) continue;
					try {
						// 使用 ListTestRunner 的命令处理器来执行单个命令
						ListTestRunner.processCommand(list, command);
					} catch (ListException e) {
						resultWriter.println("Error: " + e.getMessage());
					}
				}

				// 输出最终状态
				list.showStructure(resultWriter);
				testCase++;
			}

			System.out.println(name + " 测试完成！结果已保存到 " + outPath);
			return true;

		} catch (FileNotFoundException e) {
			System.err.println("找不到测试文件 " + inPath);
			return false;
		} catch (IOException e) {
			System.err.println("文件读写错误: " + e.getMessage());
			return false;
		}
	}

	/**
	 * 为 ResizingQueue 提供的重载：接受循环队列实例并按命令执行（只识别 enqueue/dequeue 命令）
	 */
	public static boolean runTests(String name, String testName, ResizingQueue<Integer> queue) {
		String inPath = getDataFilePath(testName);
		// 去掉 testName 的扩展名再拼接结果文件名（例如 result1000.txt -> result1000_result.txt）
		String baseName = testName;
		int dot = baseName.lastIndexOf('.');
		if (dot > 0) baseName = baseName.substring(0, dot);
		baseName = baseName.replaceAll("\\s+", "");
		String outPath = getDataFilePath(baseName + "_result.txt");

		try (Scanner fileScanner = new Scanner(new File(inPath));
			 PrintWriter resultWriter = new PrintWriter(new FileWriter(outPath))) {

			int testCase = 1;
			while (fileScanner.hasNextLine()) {
				String line = fileScanner.nextLine().trim();
				if (line.isEmpty()) continue;

				System.out.println("测试用例 " + testCase + ": " + line.substring(0, Math.min(50, line.length())) + "...");

				String[] commands = line.split("\\s+");
				for (String command : commands) {
					if (command.isEmpty()) continue;
					// 委托给 ResizingQueueTest 的 processCommand，以保持与 List 测试处理一致性
					String out = ResizingQueueTest.processCommand(queue, command);
					if (out != null) {
						// 仅在遇到 '?' 时写入输出
						resultWriter.println(out);
					}
				}
				testCase++;
			}
			System.out.println(name + " 测试完成！结果已保存到 " + outPath);
			return true;

		} catch (FileNotFoundException e) {
			System.err.println("找不到测试文件 " + inPath);
			return false;
		} catch (IOException e) {
			System.err.println("文件读写错误: " + e.getMessage());
			return false;
		}
	}
}

