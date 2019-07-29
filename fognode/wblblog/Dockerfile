FROM python:3.6-alpine

RUN adduser -D wblblog

WORKDIR /home/wblblog

COPY requirements.txt requirements.txt
RUN python -m venv venv
RUN venv/bin/pip install -r requirements.txt
RUN venv/bin/pip install gunicorn pymysql

COPY app app
COPY migrations migrations
COPY wblblog.py config.py boot.sh ./
RUN chmod a+x boot.sh

ENV FLASK_APP wblblog.py

RUN chown -R wblblog:wblblog ./
USER wblblog

EXPOSE 5432
ENTRYPOINT ["./boot.sh"]