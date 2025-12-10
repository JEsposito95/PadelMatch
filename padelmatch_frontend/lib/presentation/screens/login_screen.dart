import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'login_provider.dart';
import 'register_screen.dart';

class LoginScreen extends ConsumerWidget {
  const LoginScreen({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final emailController = TextEditingController();
    final passwordController = TextEditingController();

    // === LISTEN DEL LOGIN (SE EJECUTA SOLO CUANDO EL ESTADO CAMBIA) ===
    ref.listen<AsyncValue<String?>>(
      loginNotifierProvider,
      (previous, next) {
        next.when(
          data: (token) {
            if (token != null) {
              Navigator.pushReplacementNamed(context, '/home');
            }
          },
          error: (error, stackTrace) {
            ScaffoldMessenger.of(context).showSnackBar(
              SnackBar(
                content: Text(error.toString(),
                    style: const TextStyle(color: Colors.white)),
                backgroundColor: Colors.red,
              ),
            );
          },
          loading: () {},
        );
      },
    );

    // Este es el estado actual (loading, error o data)
    final loginState = ref.watch(loginNotifierProvider);

    return Scaffold(
      appBar: AppBar(title: const Text("Iniciar Sesión")),
      body: Padding(
        padding: const EdgeInsets.all(20.0),
        child: Column(
          children: [
            TextField(
              controller: emailController,
              decoration: const InputDecoration(
                labelText: "Email",
              ),
            ),
            TextField(
              controller: passwordController,
              decoration: const InputDecoration(
                labelText: "Contraseña",
              ),
              obscureText: true,
            ),
            const SizedBox(height: 20),

            // === BOTÓN PRINCIPAL ===
            loginState.when(
              data: (_) => ElevatedButton(
                onPressed: () async {
                  final email = emailController.text.trim();
                  final pass = passwordController.text.trim();

                  await ref
                      .read(loginNotifierProvider.notifier)
                      .login(email, pass);
                },
                child: const Text("Iniciar Sesión"),
              ),

              loading: () => const CircularProgressIndicator(),

              error: (err, stackTrace) => ElevatedButton(
                onPressed: () async {
                  final email = emailController.text.trim();
                  final pass = passwordController.text.trim();
                  await ref
                      .read(loginNotifierProvider.notifier)
                      .login(email, pass);
                },
                child: const Text("Reintentar"),
              ),
            ),

            const SizedBox(height: 20),

            // === BOTÓN DE REGISTRO ===
            TextButton(
              onPressed: () {
                Navigator.push(
                  context,
                  MaterialPageRoute(
                      builder: (_) => const RegisterScreen()),
                );
              },
              child: const Text("¿No tenés cuenta? Registrate"),
            ),
          ],
        ),
      ),
    );
  }
}
