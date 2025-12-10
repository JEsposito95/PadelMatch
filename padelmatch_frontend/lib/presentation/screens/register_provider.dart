import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:dio/dio.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';

final registerNotifierProvider =
    NotifierProvider<RegisterNotifier, AsyncValue<String?>>(() {
  return RegisterNotifier();
});

class RegisterNotifier extends Notifier<AsyncValue<String?>> {
  @override
  AsyncValue<String?> build() {
    return const AsyncValue.data(null);
  }

  Future<void> register(String name, String email, String password, String photoUrl) async {
    state = const AsyncValue.loading();

    try {
      final dio = Dio();
      final apiUrl = dotenv.env['API_URL'];

      if (apiUrl == null) {
        throw Exception("API_URL no encontrada en .env");
      }

      final response = await dio.post(
        "$apiUrl/auth/register",
        data: {
          "name": name,
          "email": email,
          "password": password,
          "photoUrl": photoUrl
        },
      );

      state = AsyncValue.data(response.data["token"]);
    } catch (e, st) {
      state = AsyncValue.error(e.toString(), st);
    }
  }
}
