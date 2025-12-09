import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:dio/dio.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';

final registerProvider =
    StateNotifierProvider<RegisterNotifier, AsyncValue<String?>>(
  (ref) => RegisterNotifier(),
);

class RegisterNotifier extends StateNotifier<AsyncValue<String?>> {
  RegisterNotifier() : super(const AsyncValue.data(null));

  final Dio _dio = Dio();

  Future<void> register(String name, String email, String password) async {
    state = const AsyncValue.loading();

    try {
      final url = "${dotenv.env['API_URL']}/api/auth/register";

      final response = await _dio.post(url, data: {
        "name": name,
        "email": email,
        "password": password,
      });

      final token = response.data["token"];
      state = AsyncValue.data(token);

    } catch (e, st) {
      state = AsyncValue.error(e.toString(), st);
    }
  }
}
