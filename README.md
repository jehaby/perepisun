# perepisun

Telegram bot that helps learning a new alphabet.

## How to run locally

Obtain a BOT API KEY via @botfather.
Install clojure, direnv, docker & compose.
Run ngrok or localtunnel server ~ngrok http 8859~.

Setup webhook:

    curl --header "Content-Type: application/json" \
        --data-raw "{\"url\":\"${WEBHOOK_CALLBACK_URI}/api/foo\"}" \
        -X POST "https://api.telegram.org/bot${BOT_API_KEY}/setWebhook"

It should respond with `{"ok":true,"result":true,"description":"Webhook was set"}`
Check it with:

    curl "https://api.telegram.org/bot${BOT_API_KEY}/getWebhookInfo"

For development I start the REPL with `cider-jack-in` (take a look at `.dir-locals.el.example`). For VS Code & Idea users it shouldn't be too hard too.


## Settings up new telegram bot via @botfather

- use `/newbot` command
- then `/setcommands`
    start - Start the bot
    stop - Stop the bot
    status - Show status
    help - Show help
    show - Show current mappings
    set - Set new mappings
    about - About the bot

## todos
- add more user friendly API
  - choose alphabet, set number of letters to write, timer with auto update of letters
- keep replies
- better help: tell that it requires permissions for reading messages in a group and how to set them;
- devops
  - prometheus metrics in the app (jetty, incoming with different statuses, response times)
  - prometheus metrics for vps
  - prometheus metrics for redis
  - prometheus metrics nginx
  - setup prometheus server
  - metrics for vps, nginx, app, http request to telegram API
- subscribe to messages only https://core.telegram.org/bots/api#update
- research if it's possible to `keep` **message** _formatting_
- more tests
- use core.async & async handlers. optionally collect several messages into one and send them in batch
