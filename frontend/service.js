const express = require('express');
const mysql = require('mysql2');
const cors = require('cors');

const app = express();
app.use(cors());

// 数据库连接配置
const db = mysql.createConnection({
    host: 'localhost',
    user: 'root',
    password: 'q5201314',
    database: 'library',
});

db.connect((err) => {
    if (err) throw err;
    console.log('数据库连接成功');
});

// 获取家庭成员及事件的接口
app.get('/api/family-members', (req, res) => {
    const userQuery = 'SELECT * FROM user';
    const eventQuery = 'SELECT account, event FROM user_event';

    db.query(userQuery, (err, users) => {
        if (err) return res.status(500).json({ error: err.message });

        db.query(eventQuery, (err2, events) => {
            if (err2) return res.status(500).json({ error: err2.message });

            // 将事件归类到用户 account
            const eventMap = {};
            events.forEach((e) => {
                if (!eventMap[e.account]) eventMap[e.account] = [];
                eventMap[e.account].push(e.event);
            });

            // 合并数据结构
            const familyMembers = users.map((user) => ({
                id: user.id,
                name: user.name,
                isAdmin: user.user_type === 'admin',
                isHome: true, // 可扩展从别的字段获取是否在家
                todos: eventMap[user.account] || [],
            }));

            res.json(familyMembers);
        });
    });
});

// 启动服务
app.listen(81, () => {
    console.log('后端服务运行在 http://localhost:81');
});
