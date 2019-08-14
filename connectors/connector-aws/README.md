# AWS Connector

This connector allows you to use [Amazon Translate](https://aws.amazon.com/translate/) to perform translation
and [Amazon Comprehend](https://aws.amazon.com/comprehend/) to perform language detections, via the Machine Translation Connector API.

The AWS region can be configured by setting the `region` property and calling `configure(Map<String,Object>)`.

An AWS account is required, and your credentials will be read using the [standard AWS approach](https://docs.aws.amazon.com/sdk-for-java/v1/developer-guide/setup-credentials.html).
Credentials will be read at creation, and every time `configure(Map<String,Object>)` is called.