import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

class main {
	public static String INPUT_FILE = "input.txt";
	public static String OUTPUT_FILE = "output.txt";

	public static int RUS_START = 1072;
	public static int FACE_SYMBOL = 1104;
	public static int RUS_COUNT_WITH_FAKE = 34;
	public static int WORD_SIZE = 4;

	public static BufferedReader reader = null;
	public static BufferedWriter writer = null;

	public static int errorNumber = 0;
	public static Map<Character, Object> rulesMap;

	public static void main(String args[]) {
		// store time
		long before = System.currentTimeMillis();

		try {
			// open files
			prepareFiles();
			// read rules
			generateMap();
			// write errors
			writeErrors();
			// close files
			closeFiles();
		} catch (FileNotFoundException e) {
			System.out.println("File not found. Error: "
					+ e.getLocalizedMessage());
		} catch (IOException e) {
			e.printStackTrace();
		}

		// show runtime
		long after = System.currentTimeMillis();
		long diff = after - before;
		System.out.println("Time is " + diff + ". Errors count is "
				+ errorNumber);
	}

	public static void prepareFiles() throws FileNotFoundException, IOException {
		reader = new BufferedReader(new FileReader(INPUT_FILE));
		writer = new BufferedWriter(new FileWriter(OUTPUT_FILE));
	}

	public static void closeFiles() throws IOException {
		reader.close();
		writer.close();
	}

	@SuppressWarnings("unchecked")
	public static void generateMapForRule(Object branch, boolean isAny,
			String word) {
		char key = word.charAt(0);
		String newWord = word.substring(1);
		if (newWord.length() > 0) {
			generateMapForRule(getBranch(branch, key), isAny, newWord);
			if (!isAny)
				generateMapForRule(branch, true, '*' + newWord);
		} else {
			getBranch(branch, key);
			if (!isAny)
				getBranch(branch, '*');
			return;
		}
	}

	public static Map<Character, Object> getBranch(Object branch, char key) {
		Map<Character, Object> hashMap = (Map<Character, Object>) branch;
		Map<Character, Object> newBranch = (Map<Character, Object>) hashMap
				.get(key);
		if (newBranch == null) {
			newBranch = new HashMap<Character, Object>();
			hashMap.put(key, newBranch);
		}

		return newBranch;
	}

	public static void generateMap() throws IOException {
		rulesMap = new HashMap<Character, Object>();

		String line;
		while ((line = reader.readLine()) != null) {
			if (line.length() == WORD_SIZE)
				generateMapForRule(rulesMap, false, line);
		}

	}

	public static void writeErrors() throws IOException {
		enumeration(RUS_START, RUS_START + RUS_COUNT_WITH_FAKE, rulesMap,
				new String());
	}

	public static void enumeration(int from, int to,
			Map<Character, Object> rules, String word) throws IOException {

		if (rules == null) {
			printAll(from, to, word);
			return;
		}

		if (rules.size() == 0)
			return;

		for (int i = from; i < to; i++)
			if (i != FACE_SYMBOL)
				if (rules.containsKey((char) i))
					enumeration(from, to,
							(Map<Character, Object>) rules.get((char) i), word
									+ (char) i);
				else
					enumeration(from, to,
							(Map<Character, Object>) rules.get('*'), word
									+ (char) i);

	}

	public static void printWord(String word) throws IOException {
		errorNumber++;
		writer.write(word);
		writer.write("\n");
	}

	public static void printAll(int from, int to, String word)
			throws IOException {
		if (word.length() < WORD_SIZE) {
			for (int i = from; i < to; i++) {
				if (i != FACE_SYMBOL)
					printAll(from, to, word + (char) i);
			}
		} else
			printWord(word);

	}

}
