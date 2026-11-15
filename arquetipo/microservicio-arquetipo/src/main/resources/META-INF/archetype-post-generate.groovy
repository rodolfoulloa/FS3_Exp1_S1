// Se ejecuta después de generar el proyecto con este arquetipo.
// Maven no empaqueta archivos llamados ".gitignore", por eso la plantilla lo
// trae como "gitignore" y aquí se renombra.
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption

def proyecto = Paths.get(request.outputDirectory, request.artifactId)
def gitignore = proyecto.resolve('gitignore')
if (Files.exists(gitignore)) {
    Files.move(gitignore, proyecto.resolve('.gitignore'), StandardCopyOption.REPLACE_EXISTING)
}

def p = request.properties
println ""
println "Microservicio ${request.artifactId} generado con microservicio-arquetipo"
println "  Entidad : ${p.entidad}  ->  /${p.recurso}  (tabla ${p.tabla})"
println "  Siguiente paso: agrega los atributos de ${p.entidad} (entidad, DTOs, Factory y copiarDatos del servicio)"
println ""
