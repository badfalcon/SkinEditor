package com.gmail.badfalcon610;

import java.util.ListResourceBundle;

public class ResourcesJA extends ListResourceBundle {
	private final static String resources[][] = {

			{ "brush", "ブラシ(B)" },
			{ "eraser", "消しゴム(E)" },
			{ "dropper", "スポイト(I)" },
			{ "bucket", "バケツ(F)" },
			{ "line", "線(L)" },
			{ "square", "長方形(R)" },
			{ "fsquare", "長方形(Shift+R)" },
			{ "ellipse", "楕円(C)" },
			{ "fellipse", "楕円(Shift+C)" },
			{ "feraser", "範囲消しゴム(Shift+E)" },
			{ "select", "範囲選択(S)" },

			{ "File", "ファイル" },
			{ "Edit", "編集" },
			{ "Settings", "設定" },
			{ "Tools", "ツール" },
			{ "Skin", "スキン" },
			{ "Help", "ヘルプ" },

			{ "New", "新規作成" },
			{ "Open", "開く" },
			{ "Save", "上書き保存" },
			{ "Save as", "名前をつけて保存" },

			{ "Undo", "元に戻す" },
			{ "Redo", "やり直し" },
			{ "Copy", "コピー" },
			{ "Cut", "切り取り" },
			{ "Paste", "貼り付け" },

			{ "change background color", "背景色の変更" },
			{ "change format color", "型の色の変更" },
			{ "reset color settings", "色設定のリセット" },
			{ "toggle format model", "型の非表示" },

			{ "Outer Head", "Outer Head" },
			{ "Outer Body", "Outer Body" },
			{ "Outer Arm(R)", "Outer Arm(R)" },
			{ "Outer Arm(L)", "Outer Arm(L)" },
			{ "Outer Leg(R)", "Outer Leg(R)" },
			{ "Outer Leg(L)", "Outer Leg(L)" },
			{ "Inner Head", "Inner Head" },
			{ "Inner Body", "Inner Body" },
			{ "Inner Arm(R)", "Inner Arm(R)" },
			{ "Inner Arm(L)", "Inner Arm(L)" },
			{ "Inner Leg(R)", "Inner Leg(R)" },
			{ "Inner Leg(L)", "Inner Leg(L)" },
			{ "Outer", "Outer" },
			{ "Inner", "Inner" },
			{ "All", "全て" },

			{ "untitled", "無題" },

			{ "preview skin", "プレビュー" },
			{ "upload and use in Minecraft", "アップロードしてマインクラフトで使う" },

			{ "older preview", "古いスキン形式(～1.7)" },
			{ "Slim Skin", "腕の細いスキン(1.8～)" },

			{ "hidepallet", "パレットを非表示にする" },
			{ "poppreview", "プレビューを別ウィンドウに表示" },

			{ "About", "Skin Editorについて" },

			{ "continue", "続ける" },
			{ "cancel", "キャンセル" },
			{ "All unsaved changes will be lost", "保存されていないものは全て失われます" },
			{ "Create new skin", "新しいスキンを作成する" },
			{ "unsupported size", "対応していないサイズです" },

			{ "Background Color", "背景色" },
			{ "format color", "型の色" },

			{ "about", "about" },
			{ "<html>Skin Editor Ver.", "<html>Skin Editor Ver." },
			{
					"<br> made by badfalcon<br><br>Great respect to <br>Minecraft Skin Edit made by Patrik Swedman <br>Minecraft made by Mojang<br><br>paint icons from Icons8 (http://icons8.com/)<br>",
					"<br>製作者：badfalcon<br><br>参考<br>Minecraft Skin Edit（作成者：Patrik Swedman） <br>Minecraft（作成者：Mojang）<br><br>アイコン：Icons8 (http://icons8.com/)<br>" },

			{ "converted successfuly", "変換に成功しました。" },
			{ "convert completed", "変換完了" },

			{ "The file exists.Overwrite?", "存在するファイルです。上書きしますか？" },
			{ "existing file", "上書き確認" },

			{ "(converted)", "(変換済み)" },

			{ "You have unsaved changes\n\nSave before closing?", "保存されていないものがあります。\n\n閉じる前に保存しますか？" },

			{ "Color Selector", "カラーセレクタ" },

			{ "showall", "すべて表示" },
			{ "hidehead", "頭を隠す" },
			{ "hidebody", "胴体を隠す" },
			{ "hidearm", "腕を隠す" },
			{ "hideleg", "足を隠す" },
			{ "hideouter", "外側を隠す" },
			{ "hideinner", "内側を隠す" },

			{ "preview", "プレビュー" },
			{ "view", "ビュー" },
			{ "show back", "後ろを表示" },
			{ "show outer skin", "外装を表示" }
	};

	protected Object[][] getContents() {
		return resources;
	}
}
