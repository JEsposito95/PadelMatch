import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'register_provider.dart';

class RegisterScreen extends ConsumerStatefulWidget {
  const RegisterScreen({super.key});

  @override
  ConsumerState<RegisterScreen> createState() => _RegisterScreenState();
}

class _RegisterScreenState extends ConsumerState<RegisterScreen> {
  final nameController = TextEditingController();
  final emailController = TextEditingController();
  final passController = TextEditingController();
  final photoUrlController = TextEditingController();

  @override
  Widget build(BuildContext context) {
    final registerState = ref.watch(registerNotifierProvider);

    return Scaffold(
      appBar: AppBar(title: const Text("Crear cuenta")),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(20.0),
        child: Column(
          children: [
            // Nombre
            TextField(
              controller: nameController,
              decoration: const InputDecoration(
                labelText: "Nombre",
              ),
            ),
            const SizedBox(height: 10),

            // Email
            TextField(
              controller: emailController,
              decoration: const InputDecoration(
                labelText: "Email",
              ),
            ),
            const SizedBox(height: 10),

            // Contraseña
            TextField(
              controller: passController,
              decoration: const InputDecoration(
                labelText: "Contraseña",
              ),
              obscureText: true,
            ),
            const SizedBox(height: 10),

            // Foto opcional
            TextField(
              controller: photoUrlController,
              decoration: const InputDecoration(
                labelText: "URL de Foto (opcional)",
              ),
            ),
            const SizedBox(height: 20),

            // ESTADO DEL REGISTER
            registerState.when(
              data: (value) {
                return ElevatedButton(
                  onPressed: () async {
                    final name = nameController.text.trim();
                    final email = emailController.text.trim();
                    final pass = passController.text.trim();
                    final photoUrl = photoUrlController.text.trim().isEmpty
                        ? null
                        : photoUrlController.text.trim();

                    if (name.isEmpty || email.isEmpty || pass.isEmpty) {
                      ScaffoldMessenger.of(context).showSnackBar(
                        const SnackBar(content: Text("Hay campos obligatorios vacíos")),
                      );
                      return;
                    }

                    await ref.read(registerNotifierProvider.notifier).register(
                      name,
                      email,
                      pass,
                      photoUrl ?? "",
                    );

                    final result = ref.read(registerNotifierProvider);

                    if (result.hasValue) {
                      ScaffoldMessenger.of(context).showSnackBar(
                        const SnackBar(content: Text("Registro exitoso")),
                      );
                      Navigator.pop(context); // Volver al login
                    }
                  },
                  child: const Text("Registrarme"),
                );
              },

              loading: () => const CircularProgressIndicator(),

              error: (err, stack) => Column(
                children: [
                  Text(err.toString(), style: const TextStyle(color: Colors.red)),
                  ElevatedButton(
                    onPressed: () async {
                      final name = nameController.text.trim();
                      final email = emailController.text.trim();
                      final pass = passController.text.trim();
                      final photoUrl = photoUrlController.text.trim();

                      await ref.read(registerNotifierProvider.notifier).register(
                        name,
                        email,
                        pass,
                        photoUrl,
                      );
                    },
                    child: const Text("Reintentar"),
                  )
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }
}
