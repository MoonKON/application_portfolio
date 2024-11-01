using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class HorizontalMoverRightReset : MonoBehaviour
{
    [SerializeField] private float moveDistance = 5f; // 可在操作台更改的移动距离
    [SerializeField] private float speed = 2f;        // 可在操作台更改的速度
    [SerializeField] private bool right = true;
    [SerializeField] private int damage = 1;
    private bool hasDealtDamage = false;
    [SerializeField] private float damageCooldown = 3f;

    private Vector3 startPosition;

    void Start()
    {
        // 记录物体的初始位置
        startPosition = transform.position;
        if (right) {
            moveDistance = moveDistance * 1;
        }
        else
        {
            moveDistance = moveDistance * -1;
        }
    }

    void Update()
    {
        // 向右移动
        transform.position = Vector3.MoveTowards(transform.position, startPosition + new Vector3(moveDistance, 0, 0), speed * Time.deltaTime);

        // 如果物体到达最右边，重置到最左边
        if (Vector3.Distance(transform.position, startPosition + new Vector3(moveDistance, 0, 0)) < 0.1f)
        {
            // 将物体重置到最左边（初始位置）
            transform.position = startPosition;
        }
    }
    private void OnTriggerEnter2D(Collider2D collision)
    {
        if (collision.CompareTag("Player"))
        {
            PlayerLife player = collision.GetComponent<PlayerLife>();
            if (player != null && !hasDealtDamage)
            {
                player.TakeDamage(damage);
                hasDealtDamage = true;
                StartCoroutine(DamageCooldown());
            }
        }
    }

    private IEnumerator DamageCooldown()
    {

        yield return new WaitForSeconds(damageCooldown);

        hasDealtDamage = false;
    }
}

