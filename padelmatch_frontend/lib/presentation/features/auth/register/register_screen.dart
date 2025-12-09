import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'register_provider.dart';

class RegisterScreen extends ConsumerStatefulWidget {
  const RegisterScreen({super.key});

  @override
  ConsumerState<RegisterScreen> createState() => _RegisterScreenState();
}

class _RegisterScreenState extends ConsumerState<RegisterScreen> {
  final _formKey = GlobalKey<FormState>();
  final nameCtrl = TextEditingController();
  final emailCtrl = TextEditingController();
  final passCtrl = TextEditingController();

  @override
  Widget build(BuildContext context) {
    final registerState = ref.watch(registerProvider);

    return Scaffold(
      appBar: AppBar(title: const Text("Crear cuenta")),
      body: Padding(
        padding: const EdgeInsets.all(16),
        child: Form(
          key: _formKey,
          child: Column(
            children: [

              TextFormField(
                controller: nameCtrl,
                decoration: const InputDecoration(labelText: "Nombre"),
                validator: (v) => v!.isEmpty ? "Ingrese un nombre" : null,
              ),

              TextFormField(
                controller: emailCtrl,
                decoration: const InputDecoration(labelText: "Email"),
                validator: (v) =>
                    v!.contains("@") ? null : "Email inválido",
              ),

              TextFormField(
                controller: passCtrl,
                decoration: const InputDecoration(labelText: "Contraseña"),
                obscureText: true,
                validator: (v) =>
                    v!.length < 6 ? "Mínimo 6 caracteres" : null,
              ),

              const SizedBox(height: 20),

              ElevatedButton(
                onPressed: registerState.isLoading
                    ? null
                    : () async {
                        if (_formKey.currentState!.validate()) {
                          await ref
                              .read(registerProvider.notifier)
                              .register(
                                nameCtrl.text,
                                emailCtrl.text,
                                passCtrl.text,
                              );

                          final token =
                              ref.read(registerProvider).value;

                          if (token != null) {
                            ScaffoldMessenger.of(context).showSnackBar(
                              const SnackBar(
                                content: Text("Registro exitoso"),
                              ),
                            );
                          }
                        }
                      },
                child: registerState.isLoading
                    ? const CircularProgressIndicator()
                    : const Text("Registrarme"),
              ),

              if (registerState.hasError) ...[
                const SizedBox(height: 15),
                Text(
                  "Error: ${registerState.error}",
                  style: const TextStyle(color: Colors.red),
                ),
              ]
            ],
          ),
        ),
      ),
    );
  }
}
