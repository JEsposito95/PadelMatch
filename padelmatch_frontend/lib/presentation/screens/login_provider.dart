import 'package:flutter_riverpod/flutter_riverpod.dart';

// PROVIDER MODERNO (Riverpod 3.x)
final loginNotifierProvider =
    NotifierProvider<LoginNotifier, AsyncValue<String?>>(
  () => LoginNotifier(),
);

class LoginNotifier extends Notifier<AsyncValue<String?>> {
  @override
  AsyncValue<String?> build() {
    // Estado inicial
    return const AsyncValue.data(null);
  }

  Future<void> login(String email, String password) async {
    state = const AsyncValue.loading();

    try {
      // simula una petición a backend
      await Future.delayed(const Duration(seconds: 2));
      const fakeToken = "fake_token_123";

      state = const AsyncValue.data(fakeToken);
    } catch (e, st) {
      state = AsyncValue.error(e, st);
    }
  }
}
