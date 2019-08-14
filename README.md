# Machine Translation

This repository provides a common Java API for connecting to Machine Translation tools (`connector-api`),
and implementations (`connectors`) of that API for the following tools:

* [Amazon Translate](https://aws.amazon.com/translate/)
* [Apache Joshua](https://joshua.apache.org)
* [Google Cloud Translation](https://cloud.google.com/translate/)
* [Moses](http://www.statmt.org/moses/)
* [REMEDI](https://github.com/ivan-zapreev/Distributed-Translation-Infrastructure)
* [SYSTRAN](http://www.systransoft.com/)

Additionally, a gRPC service (`grpc`) is provided to allow any coding language to communicate with machine translation tools via the API.