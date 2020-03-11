package com.example.databasesample;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    /**
     * 選択されたカクテルの主キーIDを表すフィールド
     */
    int _cocktaillId=-1;
    /**
     * 選択されたカクテル名を表すフィールド
     */
    String _cocktailName="";
    /**
     * カクテル名を表示するTextViewフィールド
     */
    TextView _tvCocktailName;
    /**
     * 保存ボタンフィールド
     */
    Button _btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //カクテル名を表示するTextViewを取得
        _tvCocktailName=findViewById(R.id.tvCocktailName);
        //保存ボタンを取得
        _btnSave=findViewById(R.id.btnSave);
        //カクテル用ListViewを取得
        ListView lvCocktail=findViewById(R.id.lvCocktail);
        //lvCocktailにリスナ設定メソッドを使用し、リスナクラスのインスタンスを引数として渡し登録
       lvCocktail.setOnItemClickListener(new ListItemClickListener());
    }
    /**
     * 「保存」ボタンがタップされたときの処理メソッド
     */
    public void onSavedButtonClick(View view){
        //感想欄を取得
        EditText etNote=findViewById(R.id.etNote);
        String note=etNote.getText().toString();

        //データベースヘルパーオブジェクトを作成
        DatabaseHelper helper=new DatabaseHelper(MainActivity.this);
        //データベースヘルパーオブジェクトからデータベース接続オブジェクトを取得
        SQLiteDatabase db=helper.getWritableDatabase();
        try{
            //まず、リストで選択されたカクテルのメモデータを削除。
            //その後インサートを行う。削除用SQL文字列を用意
            String sqlDelete="DELETE FROM cocktailmemo WHERE _id=?";
            //SQLの文字列を元にプリペアドステートメントを取得
            SQLiteStatement stmt=db.compileStatement(sqlDelete);
            //変数のバインド
            stmt.bindLong(1,_cocktaillId);
            //削除SQLの実行
            stmt.executeUpdateDelete();

            //インサート用SQL文字列の用意
            String SqlInsert ="INSERT INTO cocktailmemo(_id,name,note) VALUES(?,?,?)";
            //SQL文字列を元にプリペアドステートメントを取得
            stmt=db.compileStatement(SqlInsert);
            //変数のバインド
            stmt.bindLong(1,_cocktaillId);
            stmt.bindString(2,_cocktailName);
            stmt.bindString(3,note);
           stmt.executeInsert();
        }finally {
            db.close();
        }

        //カクテル名を未選択に変更
        _tvCocktailName.setText(getString(R.string.tv_name));
        //感想欄の入力値を削除
        etNote.setText("");
        //保存ボタンをタップできないように変更
        _btnSave.setEnabled(false);
    }

      /*リストがタップされた時の処理が記述されてたメンバクラス*/

    private class ListItemClickListener implements AdapterView.OnItemClickListener{
        @Override//以下イベントハンドラ
        public void onItemClick(AdapterView<?>parent,View view,int position,long id){
            //タップされた行番号をフィールドの主キーに代入。
            _cocktaillId=position;
            /**タップされた行のデータを取得。
            これがカクテル名になるので、フィールドに代入**/
            _cocktailName=(String)parent.getItemAtPosition(position);
            //カクテル名を表示する TextViewに表示カクテル名を設定。
            _tvCocktailName.setText(_cocktailName);
            //保存ボタンをタップできるように設定。
            _btnSave.setEnabled(true);
        //データベースヘルパーオブジェクトを作成
            DatabaseHelper helper=new DatabaseHelper(MainActivity.this);
            //データヘルパーオブジェクトからデータベース接続オブジェクトを取得
            SQLiteDatabase db=helper.getWritableDatabase();
            try{
                //主キーによる検索SQL文字列の用意
                String sql="SELECT * FROM cocktailmemo WHERE _id="+ _cocktaillId;
                //SQLの実行
                Cursor cursor=db.rawQuery(sql,null);
                //データベースから取得した値を格納する変数の用意
                //データがなかった時用の初期値も用意
                String note="";
                //SQL実行の戻り値であるカーソルオブジェクトをループさせて
                //データベース内のデータを取得
                while(cursor.moveToNext()){
                    //カラムのインデックス値を取得)9
                    int idxNote=cursor.getColumnIndex("note");
                    //カラムのインデックス値を元に実際のデータを取得
                    note=cursor.getString(idxNote);
                }
            //感想のEditTextの各画面部品を取得しデータベースの値を反映
                EditText etNote=findViewById(R.id.etNote);
                etNote.setText(note);
            }finally {
                db.close();
            }
        }
    }


}
