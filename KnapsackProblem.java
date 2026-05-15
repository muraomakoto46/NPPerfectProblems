package knapZaqProblem;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
/**
 * ナップザック問題
 * 
 * ↓サンプルデータ↓
 * 剣　重さ３　価値１３
 * 王冠　重さ３　価値１７
 * パソコン　重さ5　価値29
 * メダル　重さ2　価値11
 * 以上４種類の品がある。
 * 
 * 今回は重さ制限を５０としようか。
 * それぞれの品は無限にあるものとする。剣だけでも１億本でも２億本でも用意できることにする。
 * ナップザックには重さの合計が５０まで入るから、
 * ５０以内で、最も価値が高い組み合わせを考えてくれ。
 * 通常の配列で考えるとなると、いくつかの問題がある
 * １：あらかじめ膨大な要素数の二次元配列を用意することになる。
 * ２：しかもその二次元配列のほとんどが使われない。
 * ３：次に値を格納すべき場所を計算するんだけど、要素番号と重さの加算の計算式がわかりやすくない。
 * */
public class KnapsackProblem {
	//キーが重され、valueが価値を表す重さ３で、価値１３のものを四次元ポケットから取り出したとする。ならば、キーが３の要素に１３を保存する
	//もし、その後、重さ３でありながらも、価値が１４や１５など、価値が１３を超えるものがあれば、重さ３の要素に、１４とか１５って書く。
	private SortedMap<Integer,Integer> valueMap1 = new TreeMap<>();//暫定
	private SortedMap<Integer,Integer> valueMap2 = new TreeMap<>();//加算処理結果保存用
	private SortedMap<Integer,Integer> provisionalMap = new TreeMap<>();//試算用　provisional calculation 試算
	/*暫定に書き込んだものをgoodsRepositoryのものとを足して、加算処理結果保存用に保存する
	 * 一通り終わったら、加算処理結果保存用をvalueMap1に紐付けし、valueMap2に新しく空のTreeMapのオブジェクトへ紐付けする*/
	private SortedMap<Integer,Integer> goodsRepository = new TreeMap<>();
	public KnapsackProblem() {
		/*今回のサンプルデータでは重さが３であり、価値が１３のものと１７のものがある。重さが同じで価値が異なる場合は価値が高い方を登録しよう。
		 * 今回は手動です*/
		goodsRepository.put(3, 17);
		goodsRepository.put(5, 29);
		goodsRepository.put(2, 11);
	}
	public void calc() {
		//目録の中で重さが最小のものを探せ
		int minimumWeight = Integer.MAX_VALUE;
		Set<Integer> keySet = goodsRepository.keySet();
		Iterator<Integer> it = keySet.iterator();
		while(it.hasNext()) {
			int candidate = it.next();
			if(minimumWeight > candidate) {
				minimumWeight = candidate;
			}
			//System.out.println(candidate);
		}
		
		
		/*ループを何回繰り返すのか？
		 * 重さ制限 ÷ 目録の中での最小のグッズの重さ　が繰り返し回数だ*/
		int weightLimit = 50;
		int roopLimit = (int) weightLimit/minimumWeight;
		System.out.println("weightLimit:"+weightLimit+"/roopLimit:"+roopLimit+"/minimumWeight:"+minimumWeight);
		
		//初期設定
		it = keySet.iterator();
		while(it.hasNext()) {
			int key = it.next();
			valueMap1.put(key, goodsRepository.get(key));
		}
		
		//本格的なループはここから始まる
		for(int i=0;i<roopLimit;i++) {
			/*valueMap2のそれぞれのキーとvalue　＝　valueMap1の個々の重さと価値　＋　goodsRepositoryの個々の重さと価値
			 */
			//valueMap2を空にする。
			valueMap2.clear();
			
			//provisionalMapを空にする。
			provisionalMap.clear();
			
			//valueMap1の要素をそのまんまvalueMap2にコピーする。
			valueMap2.putAll(valueMap1);
			
			Set<Map.Entry<Integer,Integer>> set1 =valueMap1.entrySet();
			for(Map.Entry<Integer, Integer> entry:set1) {
				//System.out.print("entry.key:"+entry.getKey() + " / entry.value:"+entry.getValue());
				
				//valueMap1 + 目録　が、valueMap2の要素と比較して大きいか小さいかを比較する。
				//provisionalMapに加算結果を保存したい。
				Set<Map.Entry<Integer,Integer>> goodsSet = goodsRepository.entrySet();
				for(Map.Entry<Integer, Integer> goodsSetEntry:goodsSet) {
					//System.out.print("goodsSetEntry->key:"+goodsSetEntry.getKey() +" / value:"+goodsSetEntry.getValue());
					/*valueMapの特定の１つの要素と、目録の商品全てを、それぞれ足して
					 * 値を算出し、provisionalMapに保存します。*/
					Integer nextKey = entry.getKey() + goodsSetEntry.getKey();
					Integer nextValue = entry.getValue() + goodsSetEntry.getValue();
					//System.out.println("nextKey :" +nextKey + " / nextValue : " +nextValue);
					provisionalMap.put(nextKey, nextValue);
				}
				/*valueMap2とprovisionalMapとを比較します。
				 * 同じ重さなのに、価値がより高いものがprovisionalMapの方にあったなら、
				 * provisionalMapのvalueをvalueMap2のvalueに上書きします。*/
				Set<Map.Entry<Integer, Integer>> provisionalSet = provisionalMap.entrySet();
				for(Map.Entry<Integer, Integer> provisionalEntry:provisionalSet) {
					if(valueMap2.containsKey(provisionalEntry.getKey())) {
						if(valueMap2.get(provisionalEntry.getKey()) < provisionalEntry.getValue()) {
							//上書きします
							valueMap2.put(provisionalEntry.getKey(), provisionalEntry.getValue());
						}else {
							; //何もしません。
						}
					}else {
						//キーも値も存在しない場合
						//新規作成します。
						valueMap2.put(provisionalEntry.getKey(), provisionalEntry.getValue());
					}
				}
			}
			//次のループのための準備をします。
			valueMap1.clear();
			valueMap1.putAll(valueMap2);
		}
		
		
		//集計
		//valueMap1を調査してみよう。その中で、keyがweightLimit以下のものの中で、最大の価値をもつものを回答しなさい。
		Integer result = Integer.MIN_VALUE;
		Set<Map.Entry<Integer, Integer>> set1 = valueMap1.entrySet();
		for(Map.Entry<Integer, Integer> entry:set1) {
			if(entry.getKey() <= weightLimit) {
				if(entry.getValue() > result) {
					result = entry.getValue();
				}
			}
		}
		//結果発表
		System.out.println("result = : " + result);
	}
}
