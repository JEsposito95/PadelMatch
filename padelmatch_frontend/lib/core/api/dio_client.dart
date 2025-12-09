import 'package:dio/dio.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';

class DioClient {
  final Dio dio = Dio();

  DioClient() {
    dio.options.baseUrl = dotenv.env['API_URL']!;
    dio.options.headers = {
      'Content-Type': 'application/json',
    };
  }
}
