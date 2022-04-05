# notify-api
[![Clojars Project](https://img.shields.io/clojars/v/org.clojars.yjcyxky/notify-api.svg)](https://clojars.org/org.clojars.yjcyxky/notify-api)

Notify someone with something by email, dingtalk etc.

```clj
[com.github.yjcyxky/notify-api "0.1.0"]
```

## Usage

```clojure
(require '[notify-api.adapter.dingtalk :as dingtalk])

;; Setup
(def access-token "1fe1840503d9cb76277a48d79e9aa629ee6519e6437cd4aeeef194659ef4703a")
(def secret "SEC0068c84057217b3b8a32eeaa31d64b5c492116f431a7b641fa5558380661c81b")
(setup-dingtalk access-token secret)

;; Send text message
(send-text-msg! "This is a test.")

;; Send link message
(send-link-msg! "EXAMPLE" "This is a description for the link message" 
                "https://dbd0040.blob.core.windows.net/images/Example_MyTicket.jpg" 
                "https://myticket.co.uk/artists/example")

;; Send markdown message
(send-markdown-msg! "首屏会话透出的展示内容" "# 这是支持markdown的文本 \n## 标题2  \n* 列表1 \n![alt 啊](https://img.alicdn.com/tps/TB1XLjqNVXXXXc4XVXXXXXXXXXX-170-64.png)")

;; Send action card message
(send-action-card! "是透出到会话列表和通知的文案" "支持markdown格式的正文内容" "查看详情" "https://open.dingtalk.com")

```

## For Developer

### Clone the repo

```bash
git clone https://github.com/yjcyxky/notify-api
```

### Testing

`CAUTION: Some assertions may complain when you test frequently.`

```
# Test the specified namespace, such as 
lein test notify-api.adapter.dingtalk-test
```

## License

Copyright © 2021 Jingcheng Yang

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
