package jp.alhinc.calculate_sales;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CalculateSales {

	// 支店定義ファイル名
	private static final String FILE_NAME_BRANCH_LST = "branch.lst";

	// 支店別集計ファイル名
	private static final String FILE_NAME_BRANCH_OUT = "branch.out";

	//商品定義ファイル名
	private static final String FILE_NAME_COMMODITY_LST = "commodity.lst";

	//商品別集計ファイル名
	private static final String FILE_NAME_COMMODITY_OUT = "commodity.out";

	// エラーメッセージ
	private static final String UNKNOWN_ERROR = "予期せぬエラーが発生しました";
	private static final String BRANCH_FILE_NOT_EXIST = "支店定義ファイルが存在しません";
	private static final String BRANCH_FILE_INVALID_FORMAT = "支店定義ファイルのフォーマットが不正です";
	private static final String SALES_FILE_NUMBER_GAP = "売上ファイル名が連番になっていません";
	private static final String TOTAL_AMOUNT_LIMIT_ERROR = "合計⾦額が10桁を超えました";
	private static final String INVALID_FORMAT_ERROR = "のフォーマットが不正です";
	private static final String BRANCH_CODE_NOT_FOUND = "の支店コードが不正です";
	private static final String COMMODITY_FILE_NOT_EXIST = "商品定義ファイルが存在しません";
	private static final String COMMODITY_FILE_INVALID_FORMAT = "商品定義ファイルのフォーマットが不正です";
	private static final String COMMODITY_CODE_NOT_FOUND = "の商品コードが不正です";
	/**
	 * メインメソッド
	 *
	 * @param コマンドライン引数
	 */
	public static void main(String[] args) {
		//その他3-1 引数のパスを設定しているか確認
		if (args.length != 1) {
	        System.out.println(UNKNOWN_ERROR);
	        return;
	    }
		// 支店コードと支店名を保持するMap
		Map<String, String> branchNames = new HashMap<>();
		// 支店コードと売上金額を保持するMap
		Map<String, Long> branchSales = new HashMap<>();
		// 商品コードと商品名を保持するMap
		Map<String, String> commodityNames = new HashMap<>();
		// 商品コードと売上金額を保持するMap
		Map<String, Long> commoditySales = new HashMap<>();

		// 支店定義ファイル読み込み処理
		if(!readFile(args[0], FILE_NAME_BRANCH_LST, branchNames, branchSales, BRANCH_FILE_NOT_EXIST, "^[0-9]{3}$", BRANCH_FILE_INVALID_FORMAT)) {
			return;
		}

		// 商品定義ファイル読み込み処理
		if(!readFile(args[0], FILE_NAME_COMMODITY_LST, commodityNames, commoditySales, COMMODITY_FILE_NOT_EXIST, "^[a-zA-Z0-9]{8}$", COMMODITY_FILE_INVALID_FORMAT)) {
			return;
		}
		// ※ここから集計処理を作成してください。(処理内容2-1、2-2)

		//処理内容2-1
		File[] files = new File(args[0]).listFiles();

		//先にファイルの情報を格納する List(ArrayList) を宣⾔します。
		List<File> rcdFiles = new ArrayList<>();

		//filesの数だけ繰り返すことで、
		//指定したパスに存在する全てのファイル(または、ディレクトリ)の数だけ繰り返されます。
		for(int i = 0; i < files.length ; i++) {
			String fileName = files[i].getName(); //でファイル名が取得できます。

			if(files[i].isFile() && fileName.matches("^[0-9]{8}\\.rcd$")) {//ファイルであるとこと、名前の形式を同時にチェック
	            //売上ファイルの条件に当てはまったものだけ、List(ArrayList) に追加します。
				rcdFiles.add(files[i]);
			}
		}
		//エラー処理2-1 連番確認
		//売上ファイルを保持しているListをソートする
		Collections.sort(rcdFiles);

		for(int i = 0; i < rcdFiles.size() -1; i++) {
			int former = Integer.parseInt(rcdFiles.get(i).getName().substring(0, 8));
			int latter = Integer.parseInt(rcdFiles.get(i+1).getName().substring(0, 8));

		      //⽐較する2つのファイル名の先頭から数字の8⽂字を切り出し、int型に変換します。
			if((latter - former) != 1) {
				//2つのファイル名の数字を⽐較して、差が1ではなかったら、
				//エラーメッセージをコンソールに表⽰します。
				System.out.println(SALES_FILE_NUMBER_GAP);
				return;
			}
		}

		//処理内容2-2
		//rcdFilesに複数の売上ファイルの情報を格納しているので、その数だけ繰り返します。
		for(int i = 0; i < rcdFiles.size(); i++) {
			BufferedReader br = null;
			//支店定義ファイル読み込み(readFileメソッド)を参考に売上ファイルの中身を読み込みます。
			try{
		        FileReader fr = new FileReader(rcdFiles.get(i));
		        br = new BufferedReader(fr);

				List<String> fileContent = new ArrayList<>();
				String line;
				//売上ファイルの1行目には支店コード、2行目には商品コード、3行目には売上金額が入っています。
				while ((line = br.readLine()) != null) {
					//売上ファイルの中身は新しいListに保持しましょう
					fileContent.add(line);
				}

				String branchCode = fileContent.get(0);
				String commodityCode = fileContent.get(1);
				String lineSale  = fileContent.get(2);

				String fileName = files[i].getName();

				if(fileContent.size() != 3) {
				    //エラー処理2-5 売上ファイルの⾏数が3⾏ではなかった場合は、
				    //エラーメッセージをコンソールに表⽰します。
					System.out.println(fileName + INVALID_FORMAT_ERROR);
					return;
				}

				if (!branchNames.containsKey(branchCode)) {
				    //エラー処理2-3 ⽀店情報を保持しているMapに売上ファイルの⽀店コードが存在しなかった場合は、
				    //エラーメッセージをコンソールに表⽰します。
					System.out.println(fileName + BRANCH_CODE_NOT_FOUND);
					return;
				}

				if (!commodityNames.containsKey(commodityCode)) {
				    //エラー処理2-4 商品情報を保持しているMapに売上ファイルの商品コードが存在しなかった場合は、
				    //エラーメッセージをコンソールに表⽰します。
					System.out.println(fileName + COMMODITY_CODE_NOT_FOUND);
					return;
				}

				if (!lineSale.matches("^[0-9]+$")) {
				    // その他3-2 数字以外の文字が含まれている場合、エラーメッセージを表示して終了
				    System.out.println(UNKNOWN_ERROR);
				    return;
				}

				long fileSale = Long.parseLong(lineSale);
				Long saleBranchAmount = branchSales.get(branchCode) + fileSale;
				Long saleCommdityAmount = commoditySales.get(commodityCode) + fileSale;

				//エラー処理2-2  売上⾦額の合計が10桁を超えたか確認する
				if(saleBranchAmount >= 10_000_000_000L || saleCommdityAmount >= 10_000_000_000L){
					System.out.println(TOTAL_AMOUNT_LIMIT_ERROR);
					return;
				}
		        branchSales.put(branchCode, saleBranchAmount);
		        commoditySales.put(commodityCode, saleCommdityAmount);

			} catch (IOException e) {
		        // ファイルが読み込めなかった場合のエラー処理
		        System.out.println(UNKNOWN_ERROR);
		        return;
		    } finally {
				if(br != null) {
					try {
						// ファイルを閉じる
						br.close();
					} catch(IOException e) {
						System.out.println(UNKNOWN_ERROR);
						return;
					}
				}
		    }
		}

		// 支店別集計ファイル書き込み処理呼び出し
		if(!writeFile(args[0], FILE_NAME_BRANCH_OUT, branchNames, branchSales)) {
			return;
		}
		//売上別集計ファイル書き込み処理呼び出し
		if(!writeFile(args[0], FILE_NAME_COMMODITY_OUT, commodityNames, commoditySales)) {
			return;
		}
	}

	/**
	 * それぞれの定義ファイル読み込み処理
	 *
	 * @param フォルダパス
	 * @param ファイル名
	 * @param 支店コードまたは商品コードと支店名または商品名を保持するMap
	 * @param 支店コードと売上金額を保持するMap
	 * @return 読み込み可否
	 */
	private static boolean readFile(String path, String fileName, Map<String, String> names, Map<String, Long> sales, String fileNotExist,String regex, String formatError) {
		BufferedReader br = null;

		try {
			File file = new File(path, fileName);
			if(!file.exists()) {
			    //それぞれの定義ファイルが存在しない場合、コンソールにエラーメッセージを表⽰します。
				System.out.println(fileNotExist);
				return false;
			}
			FileReader fr = new FileReader(file);
			br = new BufferedReader(fr);

			String line;
			// 一行ずつ読み込む
			while((line = br.readLine()) != null) {
				// ※ここの読み込み処理を変更してください。(処理内容1-2)

				//各要素を「,」で区切り、itemsに格納する
			    String[] items = line.split(",");

				//それぞれの定義ファイルのフォーマットを確認する
				if((items.length != 2) || (!items[0].matches(regex))){
				    //商品定義ファイルの仕様が満たされていない場合、
				    //エラーメッセージをコンソールに表⽰します。
					System.out.println(formatError);
					return false;
				}

			    //namesに支店コードまたは商品コード(キー)と支店名または商品名(バリュー)を格納する
				names.put(items[0], items[1]);
				sales.put(items[0], 0L);
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
	 * それぞれの集計ファイル書き込み処理
	 *
	 * @param フォルダパス
	 * @param ファイル名
	 * @param 支店コードまたは商品コードと支店名または商品名を保持するMap
	 * @param 支店コードまたは商品コードと売上金額を保持するMap
	 * @return 書き込み可否
	 */
	private static boolean writeFile(String path, String fileName, Map<String, String> names, Map<String, Long> sales) {
		// ※ここに書き込み処理を作成してください。(処理内容3-1)
		BufferedWriter bw = null;
		try {
			File file = new File(path, fileName);

			FileWriter fw = new FileWriter(file);
			bw = new BufferedWriter(fw);

			for (String code : names.keySet()) {
				//ファイルに書き込む
				bw.write(code + "," + names.get(code) + "," + sales.get(code));
				//改行を入れる
				bw.newLine();
			}
		} catch(IOException e) {
			System.out.println(UNKNOWN_ERROR);
			return false;
		} finally {
			if(bw != null) {
				try {
					bw.close();
				} catch (IOException e) {
					System.out.println(UNKNOWN_ERROR);
					return false;
				}
			}
		}
		return true;
	}

}


