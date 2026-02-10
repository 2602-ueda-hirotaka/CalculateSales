package jp.alhinc.calculate_sales;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CalculateSales {

	// 支店定義ファイル名
	private static final String FILE_NAME_BRANCH_LST = "branch.lst";

	// 支店別集計ファイル名
	private static final String FILE_NAME_BRANCH_OUT = "branch.out";

	// エラーメッセージ
	private static final String UNKNOWN_ERROR = "支店定義ファイルが存在しません";
	private static final String FILE_NOT_EXIST = "支店定義ファイルが存在しません";
	private static final String FILE_INVALID_FORMAT = "支店定義ファイルのフォーマットが不正です";

	/**
	 * メインメソッド
	 *
	 * @param コマンドライン引数
	 */
	public static void main(String[] args) {
		// 支店コードと支店名を保持するMap
		Map<String, String> branchNames = new HashMap<>();
		// 支店コードと売上金額を保持するMap
		Map<String, Long> branchSales = new HashMap<>();

		// 支店定義ファイル読み込み処理
		if(!readFile(args[0], FILE_NAME_BRANCH_LST, branchNames, branchSales)) {
			return;
		}

		// ※ここから集計処理を作成してください。(処理内容2-1、2-2)
		//処理内容2-1
		File[] files = new File("C:\\Users\\ueda.hirotaka\\Desktop\\売上集計課題").listFiles();

		//先にファイルの情報を格納する List(ArrayList) を宣⾔します。
		List<File> rcdFiles = new ArrayList<>();


		//filesの数だけ繰り返すことで、
		//指定したパスに存在する全てのファイル(または、ディレクトリ)の数だけ繰り返されます。
		for(int i = 0; i < files.length ; i++) {
			String fileName = files[i].getName(); //でファイル名が取得できます。
			if(fileName.matches("^[0-9]+\\.rcd$")) {
	            //売上ファイルの条件に当てはまったものだけ、List(ArrayList) に追加します。
			rcdFiles.add(files[i]);
			}
		}


		//処理内容2-2
		//rcdFilesに複数の売上ファイルの情報を格納しているので、その数だけ繰り返します。
		for(int i = 0; i < rcdFiles.size(); i++) {

			//支店定義ファイル読み込み(readFileメソッド)を参考に売上ファイルの中身を読み込みます。
			try (BufferedReader br = new BufferedReader(new FileReader(rcdFiles.get(i)))) {
				//売上ファイルの1行目には支店コード、2行目には売上金額が入っています。
				String branchCode = br.readLine();
				String lineSale = br.readLine();

				//売上ファイルから読み込んだ売上金額をMapに加算していくために、型の変換を行います。
				long fileSale = Long.parseLong(lineSale);
				//long currentTotal = branchSales.get(code);
				//読み込んだ売上⾦額を加算します。
				Long saleAmount = branchSales.get(branchCode) + fileSale;

				//加算した売上⾦額をMapに追加します。
				branchSales.put(branchCode, saleAmount);

				//確認用
				//System.out.println("売上金額マップ" + branchSales);
			} catch (IOException e) {
		        // ファイルが読み込めなかった場合のエラー処理
		        System.out.println(UNKNOWN_ERROR);
		        return;
		    }
		}


		// 支店別集計ファイル書き込み処理
		//System.out.println("売上金額マップ" + branchSales);
		if(!writeFile(args[0], FILE_NAME_BRANCH_OUT, branchNames, branchSales)) {
			return;
		}

	}

	/**
	 * 支店定義ファイル読み込み処理
	 *
	 * @param フォルダパス
	 * @param ファイル名
	 * @param 支店コードと支店名を保持するMap
	 * @param 支店コードと売上金額を保持するMap
	 * @return 読み込み可否
	 */
	private static boolean readFile(String path, String fileName, Map<String, String> branchNames, Map<String, Long> branchSales) {
		BufferedReader br = null;

		try {
			File file = new File(path, fileName);
			FileReader fr = new FileReader(file);
			br = new BufferedReader(fr);

			String line;
			// 一行ずつ読み込む
			while((line = br.readLine()) != null) {
				// ※ここの読み込み処理を変更してください。(処理内容1-2)


				//各要素を「,」で区切り、itemsに格納する
			    String[] items = line.split(",");
			    //コードと支店名に分けて、空白がないようにする
			    String branchCode = items[0].trim();
			    String branchName = items[1].trim();
			    //branchNamesにcode(キー)とbranchName(バリュー)を格納する
			    branchNames.put(branchCode, branchName);
			    branchSales.put(branchCode, 0L);

			}

		} catch(IOException e) {
			System.out.println(UNKNOWN_ERROR);
			return false;
		} finally {
			// ファイルを開いている場合
			if(br != null) {
				try {
					// ファイルを閉じる
					br.close();
				} catch(IOException e) {
					System.out.println(UNKNOWN_ERROR);
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * 支店別集計ファイル書き込み処理
	 *
	 * @param フォルダパス
	 * @param ファイル名
	 * @param 支店コードと支店名を保持するMap
	 * @param 支店コードと売上金額を保持するMap
	 * @return 書き込み可否
	 */
	private static boolean writeFile(String path, String fileName, Map<String, String> branchNames, Map<String, Long> branchSales) {
		// ※ここに書き込み処理を作成してください。(処理内容3-1)
		BufferedWriter bw = null;
		try {
			File file = new File(path, fileName);
			FileWriter fw = new FileWriter(file);
			bw = new BufferedWriter(fw);


			for (String branchCode : branchNames.keySet()) {

				//支店名を取得
				String branchName = branchNames.get(branchCode);
				//合計金額を取得
				long totalSales = branchSales.get(branchCode);

				//書き込む行を作成
				String line = branchCode + "," + branchName + "," + totalSales;

				//ファイルに書き込む
				bw.write(line);
				//改行を入れる
				bw.newLine();
				System.out.println(line);
			}
		} catch(IOException e) {
			System.out.println("例外が発生しました。");
			System.out.println(e);
		} finally {
			if(bw != null) {
				try {
					bw.close();
				} catch (IOException e) {
					System.out.println("close処理中に例外が発生しました。");
					System.out.println(e);
				}
			}
		}
		return true;
	}

}
