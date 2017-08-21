package sing.greendao.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import sing.greendao.db.impl.UserDaoImpl;


public class MainActivity extends AppCompatActivity {


    private EditText etName;
    private EditText etAge;
    private RecyclerView recyclerView;

    private MyAdapter adapter;
    private User user;// 要更新的对象

    private UserDaoImpl userDaoIml;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etName = (EditText) findViewById(R.id.et_name);
        etAge = (EditText) findViewById(R.id.et_age);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MyAdapter(new MyAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position,int type) {
                user = adapter.get(position);
                if (type == 1){
                    userDaoIml.delete(user);
                    user = null;
                }else {
                    etAge.setText(user.getAge()+"");
                    etName.setText(user.getName());
                }

                updateNotes();
            }
        });
        recyclerView.setAdapter(adapter);

        userDaoIml = new UserDaoImpl(this);

        updateNotes();
    }

    public void ok(View v){
        String name = etName.getText().toString();
        String age = etAge.getText().toString();
        if (TextUtils.isEmpty(name)){
            Toast.makeText(this, "请输入姓名", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(age)){
            Toast.makeText(this, "请输入年龄", Toast.LENGTH_SHORT).show();
            return;
        }

        if (user != null){// 更新
            user.setName(name);
            user.setAge(Integer.valueOf(age));
            userDaoIml.update(user);
        }else{
            user = new User();
            user.setAge(Integer.valueOf(age));
            user.setName(name);
            userDaoIml.insert(user);
        }
        etName.setText("");
        etAge.setText("");
        user = null;

        updateNotes();
    }

    private void updateNotes() {
        adapter.setList(userDaoIml.queryAll(User.class));
    }
}